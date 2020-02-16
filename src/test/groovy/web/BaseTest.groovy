package web

import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.remote.DesiredCapabilities
import org.openqa.selenium.remote.RemoteWebDriver
import org.openqa.selenium.support.ui.FluentWait
import org.openqa.selenium.support.ui.Wait
import org.testng.annotations.AfterTest
import org.testng.annotations.BeforeTest

import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

class BaseTest {
    private static final boolean IS_REMOTE = System.getProperty('isRemote').toBoolean()
    private static final String HUB = System.getProperty('hub')
    private static final String BROWSER = System.getProperty('browser')
    private static final String BROWSER_VERSION = System.getProperty('browser.version')

    private final static ThreadLocal<WebDriver> WEB_DRIVER = new ThreadLocal<>()
    private final static ThreadLocal<Wait<WebDriver>> WAITER = new ThreadLocal<>()

    WebDriver getDriver() {
        WEB_DRIVER.get()
    }

    Wait<WebDriver> getWaiter() {
        WAITER.get()
    }

    @BeforeTest()
    void launchRemoteDriver() {
        Map labels = [
                "environment" : "oxa_env",
                "build-number": "oxa_build",
        ]
        DesiredCapabilities dc = new DesiredCapabilities()
        dc.setCapability('browserName', BROWSER)
        dc.setCapability('version', BROWSER_VERSION)
        if (IS_REMOTE) {
            dc.setCapability('sessionTimeout', '3m')
            dc.setCapability('name', 'oxagile')
//            dc.setCapability('labels', labels)
//            dc.setCapability('screenResolution', '2500x2000x24')
            dc.setCapability('enableVNC', true)
            dc.setCapability('enableVideo', true)
            dc.setCapability('enableLog', false)
            WEB_DRIVER.set(new RemoteWebDriver(new URL(HUB), dc))
            getDriver().manage().window().maximize()
        } else {
            WEB_DRIVER.set(new ChromeDriver())
            getDriver().manage().window().maximize()
        }

        Wait<WebDriver> wait = new FluentWait<>(getDriver())
                .withTimeout(15, TimeUnit.SECONDS)
                .pollingEvery(250, TimeUnit.MILLISECONDS)
        WAITER.set(wait)
    }

    @AfterTest
    void tearDownDriver() {
        if (getDriver() != null) {
            getDriver().quit()
        }
    }

    static void durationContext(String message, Closure cl) {
        def startTime = Calendar.instance.timeInMillis
        cl.call()
        def endTime = Calendar.instance.timeInMillis
        double duration = (endTime - startTime) / 1000
        println("$message: $duration; Time: ${LocalDateTime.now()}")
    }
}
