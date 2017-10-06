package Nillouise.crawler.tiebaCrawler;

import com.sun.jna.platform.unix.X11;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.service.DriverService;

import java.io.File;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;


/**
 * browser pool.Because there is no elegant way to use each tab as different browser driver,it user each window as browser driver
 */
public class BrowserPool
{
    static BlockingQueue<RemoteWebDriver> drivers = new ArrayBlockingQueue<RemoteWebDriver>(100);

    static AtomicLong browserNumber = new AtomicLong(0);
    static AtomicLong maxbrowserNumber = new AtomicLong(0);

    /**
     * @param headerless 设置是否用headerless打开浏览器
     * @return
     */
    static RemoteWebDriver initDriver(boolean headerless)
    {
        DesiredCapabilities capabilities = DesiredCapabilities.firefox();
        //这里设置使用firefox原来的profile，不设置的话selenium会自己开启一个空白的profile
        File path = new File("C:\\Users\\win7x64\\AppData\\Roaming\\Mozilla\\Firefox\\Profiles\\8jhs8tpr.default");
        FirefoxProfile ffp =  new FirefoxProfile(path);
        capabilities.setCapability(FirefoxDriver.PROFILE, ffp);
//        WebDriver driver = new RemoteWebDriver(new URL("xxx"), capabilities)//or FirefoxDriver. I ignored url for RemoteWebdriver here.
        FirefoxBinary firefoxBinary = new FirefoxBinary();
        if(headerless)
        {
            firefoxBinary.addCommandLineOptions("--headless");
        }
        System.setProperty("webdriver.gecko.driver", "C:\\code\\crawler\\geckodriver-v0.19.0-win64\\geckodriver.exe");
        FirefoxOptions firefoxOptions = new FirefoxOptions();
        firefoxOptions.setBinary(firefoxBinary);
        FirefoxDriver driver = new FirefoxDriver(firefoxBinary,ffp,capabilities);

        if(headerless)
        {
            driver.manage().window().maximize();
        }else{
            //设置窗口大小，这里导致截图时截不了全部高度（phantomjs就可以），可能要额外设置一下
            Dimension dimension = new Dimension(1280, 768);
            driver.manage().window().setSize(dimension);
        }

        browserNumber.incrementAndGet();

        maxbrowserNumber.accumulateAndGet(browserNumber.get(), Math::max);

        return driver;
    }



    static RemoteWebDriver getDriver(boolean headerless)
    {
        RemoteWebDriver driver = null;

        try
        {
            driver = drivers.poll(1, TimeUnit.SECONDS);
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }finally
        {
            if(driver==null)
            {
                driver = initDriver(headerless);
            }

            return driver;
        }
    }

    static void putDriver(RemoteWebDriver driver)
    {
        try
        {
            drivers.offer(driver,1,TimeUnit.SECONDS);
        } catch (InterruptedException e)
        {
            e.printStackTrace();
            driver.quit();
            browserNumber.decrementAndGet();
        }
    }

    static void destroy()
    {
        while(!drivers.isEmpty())
        {
            try
            {
                RemoteWebDriver driver = drivers.take();
//                driver.close();
                driver.quit();
                browserNumber.decrementAndGet();
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }

        }
    }

}
