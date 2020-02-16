package web

import org.openqa.selenium.By
import org.openqa.selenium.support.ui.ExpectedConditions
import org.testng.Assert
import org.testng.annotations.DataProvider
import org.testng.annotations.Test

class SimpleTest extends BaseTest {

    @DataProvider
    Object[][] provideUrl() {
        [
                ['http://blazedemo.com/'],
                ['https://www.google.com/'],
        ]
    }

    @Test(priority = 101, dataProvider = 'provideUrl')
    void parametrizedUrlTest(String url) {
        driver.get(url)
        Assert.assertEquals(driver.getCurrentUrl(), url, 'Incorrect URL')
    }

    @Test(priority = 102)
    void simpleTest() {
        driver.get('http://blazedemo.com/')
        driver.findElement(By.xpath("//input[@type = 'submit']")).click()
        waiter.until(ExpectedConditions
                .invisibilityOfElementLocated(By.xpath("//select[@name = 'fromPort']")))
        Assert.assertEquals(driver.findElement(By.xpath("//h3")).getText(),
                'Flights from Paris to Buenos Aires:',
                'Incorrect header')
    }
}
