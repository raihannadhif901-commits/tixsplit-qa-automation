package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.List;

public class LoginPage {
    private WebDriver driver;
    private WebDriverWait wait;

    // 1. Locator Tombol "Sign In" di Navbar (jika berada di Homepage)
    private By signInNavBtn = By.xpath("//a[contains(translate(text(),'SIGN IN','sign in'), 'sign in')] | //button[contains(translate(text(),'SIGN IN','sign in'), 'sign in')]");

    // 2. Locator Field Input (Fleksibel mencari email & password)
    private By emailField = By.cssSelector("input[type='email'], input[name='email'], input[placeholder*='email' i]");
    private By passwordField = By.cssSelector("input[type='password'], input[name='password']");

    // 3. Locator Tombol Submit "Sign In"
    private By submitBtn = By.xpath("//button[@type='submit'] | //button[contains(translate(text(),'SIGN IN','sign in'), 'sign in')]");

    // 4. Locator Pesan Error
    private By errorAlert = By.cssSelector("[role='alert'], .text-red-500, p[class*='error']");

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void openLoginPage() {
        // Coba route /sign-in
        driver.get("https://tixsplit.dibimbing.id/sign-in");

        // cek apakah ada tombol "Sign In"
        List<WebElement> navButtons = driver.findElements(signInNavBtn);
        if (!navButtons.isEmpty() && navButtons.get(0).isDisplayed()) {
            navButtons.get(0).click();
        }
    }

    public void login(String email, String password) {
        // Tunggu sampai kotak input email muncul
        WebElement emailInput = wait.until(ExpectedConditions.visibilityOfElementLocated(emailField));
        emailInput.clear();
        emailInput.sendKeys(email);

        WebElement passInput = driver.findElement(passwordField);
        passInput.clear();
        passInput.sendKeys(password);

        driver.findElement(submitBtn).click();
    }

    public boolean isErrorAppeared() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(errorAlert)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}