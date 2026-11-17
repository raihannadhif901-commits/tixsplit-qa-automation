package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class EventPage {
    private WebDriver driver;
    private WebDriverWait wait;

    // Locator Elemen Halaman Event
    private By searchField = By.cssSelector("input[placeholder*='Cari' i], input[placeholder*='Search' i], input[type='search']");
    private By eventCards = By.cssSelector("[data-testid='event-card'], .event-card, a[href*='/events/']");
    private By joinOrBookButton = By.xpath("//button[contains(translate(text(),'RSVP','rsvp'),'rsvp') or contains(translate(text(),'BELI','beli'),'beli') or contains(translate(text(),'BOOK','book'),'book')]");

    public EventPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void openEventCatalogPage() {
        driver.get("https://tixsplit.dibimbing.id/events");
    }

    public boolean isCatalogPageLoaded() {
        return wait.until(ExpectedConditions.urlContains("events"));
    }

    public void searchEvent(String keyword) {
        try {
            WebElement searchInput = wait.until(ExpectedConditions.visibilityOfElementLocated(searchField));
            searchInput.clear();
            searchInput.sendKeys(keyword);
        } catch (Exception e) {
            System.out.println("Kolom search event opsional/disesuaikan: " + e.getMessage());
        }
    }

    public int getDisplayedEventCount() {
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(eventCards));
            List<WebElement> cards = driver.findElements(eventCards);
            return cards.size();
        } catch (Exception e) {
            return 0;
        }
    }

    public void clickFirstEvent() {
        WebElement firstCard = wait.until(ExpectedConditions.elementToBeClickable(eventCards));
        try {
            firstCard.click();
        } catch (Exception e) {
            // Fallback klik menggunakan JavaScript jika terhalang animasi Next.js
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", firstCard);
        }
    }

    public boolean isJoinButtonPresent() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(joinOrBookButton)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}