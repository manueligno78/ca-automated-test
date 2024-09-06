package steps.cucumber;

import java.net.MalformedURLException;

import io.qameta.allure.Allure;
import org.openqa.selenium.WebElement;
import org.testng.Assert;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import pages.CAHomePage;

public class CAHome extends Step {
    CAHomePage caHomePage;

    @Given("I am on the Cloud Academy home page")
    public void i_am_on_the_cloud_academy_home_page() {
        caHomePage = new CAHomePage(driver, wait);
        caHomePage.get();
    }

    @When("I search for {string}")
    public void i_search_for(String query) {
        Allure.parameter("query", query);
        caHomePage.search(query);
    }

    @Then("I should see a list of courses related to {string}")
    public void i_should_see_a_list_of_courses_related_to(String query) {
        Allure.parameter("query", query);
        wait.until(driver -> !caHomePage.getSearchResultTitles().isEmpty());
        Assert.assertFalse(caHomePage.getSearchResultTitles().stream().noneMatch(element -> element.getText().contains(query)));
    }

    @Before
    public void beforeScenario() throws MalformedURLException {
        setUp();
    }

    @After
    public void afterScenario() {
        tearDown();
    }

    @When("I click on the pricing link")
    public void iClickOnThePricingLink() {
        caHomePage.clickPricing();
    }

    @Then("I land on the pricing page")
    public void iLandOnThePricingPage() {
        wait.until(driver -> driver.getCurrentUrl().contains("https://platform.qa.com/pricing/"));
    }

    @Then("all pricing plans should be visible")
    public void allPricingPlansShouldBeVisible() {
        wait.until(driver -> !caHomePage.getPricingPlans().isEmpty());
    }

    @When("I click on the Start Now button for the Small Teams plan")
    public void iClickOnTheStartNowButtonForTheSmallTeamsPlan() {
        caHomePage.clickStartNowSmallTeams();
    }

    @Then("I expect to land on the payment page")
    public void iExpectToLandOnThePaymentPage() {
        wait.until(driver -> driver.getCurrentUrl().contains("https://platform.qa.com/checkout-beta/self-serve/account/?annually=1&seats=5"));
    }

    @And("I should see all the required fields to fill in the form")
    public void iShouldSeeAllTheRequiredFieldsToFillInTheForm() {
        wait.until(driver -> caHomePage.getNameField().isDisplayed());
        wait.until(driver -> caHomePage.getEmailField().isDisplayed());
        wait.until(driver -> caHomePage.getPasswordField().isDisplayed());
        wait.until(driver -> caHomePage.getCompanyField().isDisplayed());
        wait.until(driver -> caHomePage.getPhoneField().isDisplayed());
    }
}