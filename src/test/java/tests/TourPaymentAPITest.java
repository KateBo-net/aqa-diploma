package tests;

import com.codeborne.selenide.logevents.SelenideLogger;
import data.CardInfo;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;

import static api.RequestGenerator.*;
import static data.SQLHelper.*;
import static data.DataHelper.generateValidCardInfo;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TourPaymentAPITest {
    private final String expectedApproved = "APPROVED";
    private final String expectedDeclined = "DECLINED";
    private final String expectedAmount = "4500000";

    @BeforeAll
    static void setupAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @AfterEach
    public void tearDown(){
        cleanDB();
    }

    @AfterAll
    public static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }

    @Test
    @DisplayName("Отправка запроса на оплату тура по дебетовой карте со статусом “APPROVED”")
    public void payByCardStatusApproved() {
        CardInfo approvedCard = generateValidCardInfo(0);

        String paymentStatusResponse = postPaymentByCard(approvedCard);
        String paymentStatusDB = getStatusFromPaymentEntity();
        String paymentAmount = getAmountFromPaymentEntity();

        assertAll(
                () -> assertEquals(expectedApproved, paymentStatusResponse),
                () -> assertEquals(expectedApproved, paymentStatusDB),
                () -> assertEquals(expectedAmount, paymentAmount)
        );
    }

    @Test
    @DisplayName("Отправка запроса на оплату тура по дебетовой карте со статусом “DECLINED”")
    public void payByCardStatusDeclined() {
        CardInfo approvedCard = generateValidCardInfo(1);

        String paymentStatusResponse = postPaymentByCard(approvedCard);
        String paymentStatusDB = getStatusFromPaymentEntity();
        String paymentAmount = getAmountFromPaymentEntity();

        assertAll(
                () -> assertEquals(expectedDeclined, paymentStatusResponse),
                () -> assertEquals(expectedDeclined, paymentStatusDB),
                () -> assertEquals(expectedAmount, paymentAmount)
        );
    }

    @Test
    @DisplayName("Отправка запроса на оплату тура в кредит по карте со статусом “APPROVED”")
    public void payByCreditStatusApproved() {
        CardInfo approvedCard = generateValidCardInfo(0);

        String paymentStatusResponse = postPaymentByCredit(approvedCard);
        String paymentStatusDB = getStatusFromCreditEntity();

        assertAll(
                () -> assertEquals(expectedApproved, paymentStatusResponse),
                () -> assertEquals(expectedApproved, paymentStatusDB)
        );
    }

    @Test
    @DisplayName("Отправка запроса на оплату тура в кредит по карте со статусом “DECLINED”")
    public void payByCreditStatusDeclined() {
        CardInfo approvedCard = generateValidCardInfo(1);

        String paymentStatusResponse = postPaymentByCredit(approvedCard);
        String paymentStatusDB = getStatusFromCreditEntity();

        assertAll(
                () -> assertEquals(expectedDeclined, paymentStatusResponse),
                () -> assertEquals(expectedDeclined, paymentStatusDB)
        );
    }
}
