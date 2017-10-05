package com.mozilla.example;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class HeadlessFirefoxSeleniumExample {
  public static void main(String [] args) throws IOException
  {
    FirefoxBinary firefoxBinary = new FirefoxBinary();
    firefoxBinary.addCommandLineOptions("--headless");
    System.setProperty("webdriver.gecko.driver", "C:\\code\\crawler\\geckodriver-v0.19.0-win64\\geckodriver.exe");
    FirefoxOptions firefoxOptions = new FirefoxOptions();
    firefoxOptions.setBinary(firefoxBinary);
    FirefoxDriver driver = new FirefoxDriver(firefoxOptions);
    try {
      driver.get("http://www.google.com");
      driver.manage().timeouts().implicitlyWait(4,
          TimeUnit.SECONDS);
//      WebElement queryBox = driver.findElement(By.name("q"));
//      queryBox.sendKeys("headless firefox");
//      WebElement searchBtn = driver.findElement(By.name("btnK"));
//      searchBtn.click();
//      WebElement iresDiv = driver.findElement(By.id("ires"));
//      iresDiv.findElements(By.tagName("a")).get(0).click();



        System.out.println(driver.getPageSource());
    } finally {
        File screenshotAs = driver.getScreenshotAs(OutputType.FILE);
        FileUtils.copyFile(screenshotAs, new File("screenshot.png"));

        driver.quit();
    }
  }
}
