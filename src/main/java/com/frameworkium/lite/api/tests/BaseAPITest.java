package com.frameworkium.lite.api.tests;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Tag;

@Tag("base-api")
public abstract class BaseAPITest {

    protected final Logger logger = LogManager.getLogger(this);
}
