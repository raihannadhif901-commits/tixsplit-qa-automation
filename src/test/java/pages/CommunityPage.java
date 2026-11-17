package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class CommunityPage {
    private WebDriver driver;
    private WebDriverWait wait;

    private By communityHeader = By.xpath("//h1[contains(translate(text(),'COMMUNITY','community'),'community')] | //h2[contains(translate(text(),'COMMUNITY','community'),'community')]");
    private By createCommunityBtn = By.xpath("//a[contains(@href,'/communities/create')] | //button[contains(translate(text(),'CREATE','create'),'create')]");

    public CommunityPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void openCommunityListPage() {
        driver.get("https://tixsplit.dibimbing.id/communities");
    }

    public boolean isCommunityPageLoaded() {
        return wait.until(ExpectedConditions.urlContains("communities"));
    }

    public void clickCreateCommunity() {
        try {
            WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(createCommunityBtn));
            btn.click();
        } catch (Exception e) {
            driver.get("https://tixsplit.dibimbing.id/communities/create");
        }
    }
}