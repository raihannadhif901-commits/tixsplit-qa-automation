package tests.ui;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.LoginPage;

public class LoginUiTest {
    private WebDriver driver;
    private LoginPage loginPage;

    @BeforeMethod
    public void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--remote-allow-origins=*");

        // OTOMATIS: Jika berjalan di GitHub Actions (CI), aktifkan mode headless tanpa layar
        if (System.getenv("CI") != null) {
            options.addArguments("--headless=new");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--window-size=1920,1080");
        }

        driver = new ChromeDriver(options);
        loginPage = new LoginPage(driver);
    }

    @Test(description = "UI: Validasi gagal masuk saat Sign In dengan password salah")
    public void testLoginWithWrongPassword() {
        loginPage.openLoginPage();
        loginPage.login("raihannadhif14@gmail.com", "PasswordSalah123!");

        // Assert: user tidak lolos ke dashboard tetap di URL sign-in
        boolean isStillOnSignIn = driver.getCurrentUrl().contains("sign-in") || driver.getCurrentUrl().contains("signin");
        Assert.assertTrue(isStillOnSignIn || loginPage.isErrorAppeared(),
                "User seharusnya tidak berhasil masuk ke dashboard dengan password salah!");
    }

    @Test(description = "UI: Validasi berhasil masuk saat Sign In dengan kredensial valid")
    public void testLoginSuccess() {
        loginPage.openLoginPage();

        // 1. kredensial
        loginPage.login("raihannadhif14@gmail.com", "Password123!");

        // 2. waktu Selenium menunggu sampai URL berubah
        org.openqa.selenium.support.ui.WebDriverWait wait =
                new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(10));

        boolean isRedirected = wait.until(
                org.openqa.selenium.support.ui.ExpectedConditions.not(
                        org.openqa.selenium.support.ui.ExpectedConditions.urlContains("sign-in")
                )
        );

        // 3. Assert setelah halaman selesai berpindah
        Assert.assertTrue(isRedirected, "User seharusnya berhasil login dan dialihkan ke halaman utama/dashboard!");
        System.out.println(">>> Halaman Berhasil Dialihkan ke: " + driver.getCurrentUrl());
    }

    // ==========================================
    // ADVANCED: DATA-DRIVEN TESTING (DDT)
    // ==========================================
    @org.testng.annotations.DataProvider(name = "invalidLoginData")
    public Object[][] getInvalidLoginData() {
        return new Object[][] {
                {"email.ngawur@mail.com", "Password123!", "Skenario: Email tidak terdaftar"},
                {"user@mail.com", "PasswordSalahTotal999!", "Skenario: Password salah"},
                {"bukan-format-email", "Password123!", "Skenario: Format email invalid"}
        };
    }

    @Test(dataProvider = "invalidLoginData", priority = 4,
            description = "UI DDT: Menguji beragam skenario kredensial salah dalam satu runner")
    public void testLoginDataDrivenInvalid(String email, String password, String scenario) {
        System.out.println(">>> Menjalankan " + scenario);
        loginPage.openLoginPage();
        loginPage.login(email, password);

        // Verifikasi bahwa ketiga data salah ini semuanya berhasil ditolak oleh sistem
        boolean isBlocked = driver.getCurrentUrl().contains("sign-in") || loginPage.isErrorAppeared();
        Assert.assertTrue(isBlocked, "User seharusnya gagal login untuk " + scenario);
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}