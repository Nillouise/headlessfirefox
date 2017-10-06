package Nillouise.crawler.tiebaCrawler;


import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


class Browser implements Runnable
{
    RemoteWebDriver driver;

    public Browser(RemoteWebDriver driver)
    {
        this.driver = driver;
    }

    @Override
    public void run()
    {
        try{

            driver.get("http://www.baidu.com");
        }finally
        {
            saveScreen(Thread.currentThread()+"baidu");
            BrowserPool.putDriver(driver);
        }
    }

    void saveScreen(String file)
    {
        File screenshotAs = driver.getScreenshotAs(OutputType.FILE);
        try
        {
            FileUtils.copyFile(screenshotAs, new File("resource/"+file+".png"));
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}



public class MultiThread
{

    void execute()
    {
        long pretime = System.currentTimeMillis();

        //这里填1000会把整个系统都爆掉
        int openBrowserNumber = 1000;

        ExecutorService pool = Executors.newCachedThreadPool();
        List<Thread> list = new ArrayList<>();

        for (int i = 0; i < openBrowserNumber; i++)
        {
            Browser browser = new Browser(BrowserPool.getDriver(true));
            list.add(new Thread(browser));
            list.get(list.size()-1).start();
        }

        for (Thread thread : list)
        {
            try
            {
                thread.join();
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
        BrowserPool.destroy();

        System.out.format("最多开启了%d个浏览器,运行结束后，还有%d个浏览器\n",BrowserPool.maxbrowserNumber.get(), BrowserPool.browserNumber.get());
        System.out.format("运行%d个线程需要%d时间",openBrowserNumber, (System.currentTimeMillis()-pretime)/1000);
    }

    public static void main(String[] args)
    {

        MultiThread crawler = new MultiThread();
        crawler.execute();

    }

}
