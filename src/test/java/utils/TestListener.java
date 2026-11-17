package utils;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TestListener implements ITestListener {

    @Override
    public void onTestFailure(ITestResult result) {
        Object currentClass = result.getInstance();

        // Hanya ambil screenshot jika test yang gagal adalah pengujian UI (memiliki field 'driver')
        try {
            java.lang.reflect.Field driverField = currentClass.getClass().getDeclaredField("driver");
            driverField.setAccessible(true);
            WebDriver driver = (WebDriver) driverField.get(currentClass);

            if (driver != null) {
                System.out.println(">>> [LISTENER] Test UI Gagal: " + result.getName() + " -> Mengambil Screenshot...");
                File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
                String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String destPath = "target/screenshots/" + result.getName() + "_" + timestamp + ".png";

                Files.createDirectories(Paths.get("target/screenshots/"));
                Files.copy(srcFile.toPath(), Paths.get(destPath));
                System.out.println(">>> Screenshot tersimpan di: " + destPath);
            }
        } catch (NoSuchFieldException ignored) {
            // Abaikan jika test yang gagal adalah API test (tidak memakai Selenium driver)
        } catch (Exception e) {
            System.out.println("Listener error: " + e.getMessage());
        }
    }
}