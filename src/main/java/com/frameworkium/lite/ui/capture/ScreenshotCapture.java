package com.frameworkium.lite.ui.capture;

import static com.frameworkium.lite.common.properties.Property.CAPTURE_THREADS;
import static com.frameworkium.lite.common.properties.Property.CAPTURE_URL;
import static com.frameworkium.lite.common.properties.Property.SUT_NAME;
import static com.frameworkium.lite.common.properties.Property.SUT_VERSION;
import static com.frameworkium.lite.common.properties.Property.THREADS;

import static org.apache.http.HttpStatus.SC_CREATED;

import com.frameworkium.lite.ui.capture.model.Command;
import com.frameworkium.lite.ui.capture.model.message.CreateExecution;
import com.frameworkium.lite.ui.capture.model.message.CreateScreenshot;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/** Takes and sends screenshots to "Capture" asynchronously. */
public class ScreenshotCapture {

    private static final Logger logger = LogManager.getLogger();

    private static final boolean isConvertAvailable = isConvertAvailable();

    public static boolean isConvertAvailable() {
        try {
            Process p = new ProcessBuilder("convert", "-version")
                    .redirectErrorStream(true)
                    .start();
            boolean finished = p.waitFor(2, TimeUnit.SECONDS);
            if (!finished) {
                p.destroyForcibly();
                return false;
            }
            return p.exitValue() == 0;
        } catch (IOException e) {
            return false;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    /** Executor pool for async sending of screenshot messages to capture. */
    private static final List<ExecutorService> sendScreenshotExecutors = new ArrayList<>();

    static {
        for (int i = 0; i < CAPTURE_THREADS.getIntWithDefault(4); i++) {
            sendScreenshotExecutors.add(Executors.newSingleThreadExecutor());
        }
    }

    private static final ExecutorService compressScreenshotExecutor =
            Executors.newFixedThreadPool(THREADS.getIntWithDefault(4));

    private final String testID;
    private final String executionID;

    /** Prevent multiple final state screenshots from being sent. */
    private boolean finalScreenshotSent = false;

    private static final Set<String> FINAL_STATES = Set.of("pass", "fail", "skip");

    public ScreenshotCapture(String testID) {
        logger.debug("About to initialise Capture execution for {}", testID);
        this.testID = testID;
        this.executionID = createExecution(new CreateExecution(testID, getNode()));
        logger.debug("Capture executionID={}", executionID);
    }

    private String createExecution(CreateExecution createExecution) {
        try {
            return getRequestSpec()
                    .body(createExecution)
                    .when()
                    .post(CaptureEndpoint.EXECUTIONS.getUrl())
                    .then()
                    .statusCode(SC_CREATED)
                    .extract()
                    .path("executionID")
                    .toString();
        } catch (Throwable t) {
            logger.error("Unable to create Capture execution.", t);
            return null;
        }
    }

    private RequestSpecification getRequestSpec() {
        return RestAssured.given().relaxedHTTPSValidation().contentType(ContentType.JSON);
    }

    private String getNode() {
        try {
            return InetAddress.getLocalHost().getCanonicalHostName();
        } catch (UnknownHostException e) {
            return "n/a";
        }
    }

    public static boolean isRequired() {
        return CAPTURE_URL.isSpecified() && SUT_NAME.isSpecified() && SUT_VERSION.isSpecified();
    }

    public void takeAndSendScreenshot(Command command, WebDriver driver) {
        takeAndSendScreenshotWithError(command, driver, null);
    }

    public void takeAndSendScreenshotWithError(
            Command command, WebDriver driver, String errorMessage) {

        if (executionID == null) {
            logger.error(
                    "Can't send Screenshot. Capture didn't initialise execution for test: {}",
                    testID);
            return;
        }

        if (finalScreenshotSent) {
            logger.debug(
                    "Final screenshot already sent for {}, skipping {}",
                    executionID,
                    command.action);
            return;
        }

        finalScreenshotSent = FINAL_STATES.contains(command.action);

        // Take screenshot and other info from driver
        File screenshotFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        screenshotFile.deleteOnExit();
        String currentURL = driver.getCurrentUrl();

        // Compress it on a separate thread
        Future<String> future = compressScreenshotExecutor.submit(
                () -> getBase64ScreenshotAndDeleteFile(screenshotFile));

        // Send it to capture on a separate single thread
        // Hashing the execution ID so ensure they are sent sequentially for each execution
        int index = Math.abs(executionID.hashCode()) % sendScreenshotExecutors.size();
        sendScreenshotExecutors.get(index).execute(() -> {
            try {
                var createScreenshotMessage = new CreateScreenshot(
                        executionID, command, currentURL, errorMessage, future.get());
                sendScreenshot(createScreenshotMessage);
            } catch (Exception e) {
                logger.warn(e);
            }
        });
    }

    private String getBase64ScreenshotAndDeleteFile(File imageFile) throws IOException {
        Path outputFile = null;
        try {
            if (!isConvertAvailable) {
                return Base64.getEncoder().encodeToString(Files.readAllBytes(imageFile.toPath()));
            }

            outputFile = Files.createTempFile("screenshot", ".png");
            outputFile.toFile().deleteOnExit();
            limitColoursToCompressImage(imageFile, outputFile);
            return Base64.getEncoder().encodeToString(Files.readAllBytes(outputFile));
        } catch (IOException e) {
            logger.warn("Error compressing screenshot", e);
            return Base64.getEncoder().encodeToString(Files.readAllBytes(imageFile.toPath()));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        } finally {
            try {
                Files.delete(imageFile.toPath());
                if (outputFile != null) {
                    Files.delete(outputFile.toAbsolutePath());
                }
            } catch (IOException e) {
                logger.warn("Failed to delete temporary screenshot files", e);
            }
        }
    }

    private static void limitColoursToCompressImage(File imageFile, Path outputFile)
            throws InterruptedException, IOException {
        String outputPath = outputFile.toAbsolutePath().toString();
        new ProcessBuilder(
                        "convert",
                        imageFile.getAbsolutePath(),
                        "+dither",
                        "-colors",
                        "128",
                        outputPath)
                .start()
                .waitFor(30, TimeUnit.SECONDS);
    }

    private void sendScreenshot(CreateScreenshot createScreenshotMessage) {
        try {
            getRequestSpec()
                    .body(createScreenshotMessage)
                    .when()
                    .post(CaptureEndpoint.SCREENSHOT.getUrl())
                    .then()
                    .log()
                    .ifError()
                    .statusCode(SC_CREATED);
        } catch (Throwable t) {
            logger.warn("Failed sending screenshot to Capture for {}", testID);
            logger.debug(t);
        }
    }

    /**
     * Waits up to 2 minutes to send any remaining Screenshot messages.
     */
    public static void processRemainingBacklog() {

        sendScreenshotExecutors.forEach(ExecutorService::shutdown);

        if (!isRequired()) {
            return;
        }

        logger.info("Processing remaining Screenshot Capture backlog...");
        var totalRemainingJobs = new AtomicInteger(0);
        var terminationFutures = sendScreenshotExecutors.stream()
                .map(executor -> CompletableFuture.runAsync(() -> {
                    try {
                        if (!executor.awaitTermination(2, TimeUnit.MINUTES)) {
                            totalRemainingJobs.addAndGet(executor.shutdownNow().size());
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException(e);
                    }
                }))
                .toList();

        // Wait for all termination futures to complete
        try {
            CompletableFuture.allOf(terminationFutures.toArray(new CompletableFuture[0]))
                    .get(3, TimeUnit.MINUTES);
        } catch (Exception e) {
            logger.error("CompletableFuture timed out.");
        }
        if (totalRemainingJobs.get() > 0) {
            logger.error("Shutdown timed out. {} screenshots not sent.", totalRemainingJobs.get());
        } else {
            logger.info("Finished processing backlog.");
        }
    }
}
