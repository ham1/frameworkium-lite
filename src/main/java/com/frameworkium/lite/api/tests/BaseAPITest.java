package com.frameworkium.lite.api.tests;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestInstance;

@Tag("base-api")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class BaseAPITest {

    protected final Logger logger = LogManager.getLogger(this);
}
