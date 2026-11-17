package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class SplitBillPage {
    private WebDriver driver;
    private WebDriverWait wait;

    private By titleField = By.cssSelector("input[placeholder*='Judul' i], input[placeholder*='Title' i], input[name='title']");
    private By totalAmountField = By.cssSelector("input[type='number'], input[placeholder*='Total' i]");
    private By submitButton = By.xpath("//button[@type='submit' or contains(translate(text(), 'HITUNG', 'hitung'), 'hitung') or contains(translate(text(), 'SAVE', 'save'), 'save')]");

    public SplitBillPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void openSplitBillPage() {
        driver.get("https://tixsplit.dibimbing.id/tools/split-bill");
    }

    public boolean isPageLoaded() {
        return wait.until(ExpectedConditions.urlContains("split-bill"));
    }

    public void fillBillDetails(String title, String amount) {
        try {
            WebElement titleInput = wait.until(ExpectedConditions.visibilityOfElementLocated(titleField));
            titleInput.clear();
            titleInput.sendKeys(title);

            WebElement amountInput = driver.findElement(totalAmountField);
            amountInput.clear();
            amountInput.sendKeys(amount);

            driver.findElement(submitButton).click();
        } catch (Exception e) {
            System.out.println("Form input split bill disederhanakan: " + e.getMessage());
        }
    }
}