package tests;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;

import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.Condition.*;

import page.PaymentPage;
import page.TourPage;
import data.CardInfo;

import static data.DataHelper.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TourPaymentUITest {

    private final String approvedCardMsg = "Успешно";
    private final String declinedCardMsg = "Ошибка";
    private final String wrongFormatMsg = "Неверный формат";
    private final String expiredCard = "Истёк срок действия карты";
    private final String wrongDate = "Неверно указан срок действия карты";
    private final int cvcCount = 3;
    private final int cardNumberCount = 16;
    private final int monthCount = 12;
    private final int shift = 3;
    TourPage tourPage;

    @BeforeAll
    static void setupAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @BeforeEach
    public void setup() {
        tourPage = open(System.getProperty("sut.url"), TourPage.class);
    }

    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }

    @Test
    @DisplayName("Оплата тура картой со статусом «APPROVED»")
    public void paymentForTourByCardStatusApproved() {
        CardInfo approvedCard = generateValidCardInfo(0);

        PaymentPage paymentPage = tourPage.payByCard();
        String notificationMsg = paymentPage.validPayment(approvedCard);

        assertTrue(notificationMsg.contains(approvedCardMsg));
    }

    @Test
    @DisplayName("Оплата тура картой со статусом «DECLINED»")
    public void paymentForTourByCardStatusDeclined() {
        CardInfo declinedCard = generateValidCardInfo(1);

        PaymentPage paymentPage = tourPage.payByCard();
        String notificationMsg = paymentPage.validPayment(declinedCard);

        assertTrue(notificationMsg.contains(declinedCardMsg));
        //отображается уведомление успешно вместо ошибки
    }

    @Test
    @DisplayName("Оплата тура картой с незарегистрированным номером")
    public void paymentForTourByNotRegisteredCard() {
        CardInfo unregisteredCard = new CardInfo(generateNumber(cardNumberCount), generateMonth(), generateYear(shift), generateOwner(), generateNumber(cvcCount));

        PaymentPage paymentPage = tourPage.payByCard();
        String notificationMsg = paymentPage.validPayment(unregisteredCard);

        assertTrue(notificationMsg.contains(declinedCardMsg));
        //отображаются оба уведомления поверх ошибка, снизу успешно, поэтому ловиться первый элемент успешно
    }

    @Test
    @DisplayName("Отправка пустой формы")
    public void sendEmptyForm() {
        PaymentPage paymentPage = tourPage.payByCard();
        paymentPage.sendForm();
        ElementsCollection invalidMsgCollection = paymentPage.getCollectionInvalidMsg();

        assertEquals(5, invalidMsgCollection.size());
    }

    @Test
    @DisplayName("Валидация данных в поле «Номер карты»: 16 цифр»")
    public void validationFieldCardNumber16Digits() {
        CardInfo validCardNumber = new CardInfo(generateNumber(16), "", "", "", "");

        PaymentPage paymentPage = tourPage.payByCard();
        paymentPage.fillCardData(validCardNumber);
        SelenideElement invalidMsg = paymentPage.getInvalidMsgCardNumber();

        invalidMsg.shouldNot(exist);
    }

    @Test
    @DisplayName("Валидация данных в поле «Номер карты»: 15 цифр»")
    public void validationFieldCardNumber15Digits() {
        CardInfo invalidCardNumber = new CardInfo(generateNumber(cardNumberCount - 1), "", "", "", "");

        PaymentPage paymentPage = tourPage.payByCard();
        paymentPage.fillCardData(invalidCardNumber);
        SelenideElement invalidMsg = paymentPage.getInvalidMsgCardNumber();

        invalidMsg
                .shouldBe(visible)
                .shouldHave(text(wrongFormatMsg));
    }

    @Test
    @DisplayName("Валидация данных в поле «Номер карты»: 17 цифр»")
    public void validationFieldCardNumber17Digits() {
        CardInfo invalidCardNumber = new CardInfo(generateNumber(cardNumberCount + 1), "", "", "", "");

        PaymentPage paymentPage = tourPage.payByCard();
        paymentPage.fillCardData(invalidCardNumber);
        SelenideElement invalidMsg = paymentPage.getInvalidMsgCardNumber();

        //добавляем к количеству цифр в номере карты 3 пробела разделителя
        assertEquals(cardNumberCount + 3, paymentPage.getFieldValueCardNumber().length());
        invalidMsg.shouldNot(exist);
    }

    @Test
    @DisplayName("Валидация данных в поле «Номер карты»: спец. символы")
    public void validationFieldCardNumberSpecialSymbols() {
        String cardNumber = generateSymbolString(cardNumberCount);
        CardInfo invalidCardNumber = new CardInfo(cardNumber, "", "", "", "");

        PaymentPage paymentPage = tourPage.payByCard();
        paymentPage.fillCardData(invalidCardNumber);
        SelenideElement invalidMsg = paymentPage.getInvalidMsgCardNumber();


        assertEquals(0, paymentPage.getFieldValueCardNumber().length());
        invalidMsg
                .shouldBe(visible)
                .shouldHave(text(wrongFormatMsg));
    }

    @Test
    @DisplayName("Валидация данных в поле «Номер карты»: буквы")
    public void validationFieldCardNumberLetters() {
        String cardNumber = generateLastName();
        CardInfo invalidCardNumber = new CardInfo(cardNumber, "", "", "", "");

        PaymentPage paymentPage = tourPage.payByCard();
        paymentPage.fillCardData(invalidCardNumber);
        SelenideElement invalidMsg = paymentPage.getInvalidMsgCardNumber();


        assertEquals(0, paymentPage.getFieldValueCardNumber().length());
        invalidMsg
                .shouldBe(visible)
                .shouldHave(text(wrongFormatMsg));
    }

    @Test
    @DisplayName("Валидация данных в поле “Месяц”: число от 1 до 12 в двузначном формате")
    public void validationFieldMonthValidValue() {
        CardInfo validMonth = new CardInfo("", generateMonth(), "", "", "");

        PaymentPage paymentPage = tourPage.payByCard();
        paymentPage.fillCardData(validMonth);
        SelenideElement invalidMsg = paymentPage.getInvalidMsgMonth();

        invalidMsg.shouldNot(exist);

    }

    @Test
    @DisplayName("Валидация данных в поле “Месяц”: число 00")
    public void validationFieldMonth00Value() {
        CardInfo invalidMonth = new CardInfo("", "00", "", "", "");

        PaymentPage paymentPage = tourPage.payByCard();
        paymentPage.fillCardData(invalidMonth);
        SelenideElement invalidMsg = paymentPage.getInvalidMsgMonth();

        invalidMsg
                .shouldBe(visible)
                .shouldHave(text(wrongFormatMsg));
    }

    @Test
    @DisplayName("Валидация данных в поле “Месяц”: число 13")
    public void validationFieldMonth13Value() {
        CardInfo invalidMonth = new CardInfo(generateNumber(cardNumberCount), String.valueOf(monthCount + 1), generateYear(shift), generateOwner(), generateNumber(cvcCount));

        PaymentPage paymentPage = tourPage.payByCard();
        paymentPage.fillCardData(invalidMonth);
        SelenideElement invalidMsg = paymentPage.getInvalidMsgMonth();

        invalidMsg
                .shouldBe(visible)
                .shouldHave(text(wrongDate));
    }

    @Test
    @DisplayName("Валидация данных в поле “Месяц”: цифры от 1 до 9")
    public void validationFieldMonthDigitsValue() {
        CardInfo invalidMonth = new CardInfo(generateNumber(cardNumberCount), generateNumber(1), generateYear(shift), generateOwner(), generateNumber(cvcCount));

        PaymentPage paymentPage = tourPage.payByCard();
        paymentPage.fillCardData(invalidMonth);
        SelenideElement invalidMsg = paymentPage.getInvalidMsgMonth();

        invalidMsg
                .shouldBe(visible)
                .shouldHave(text(wrongFormatMsg));
    }

    @Test
    @DisplayName("Валидация данных в поле “Месяц”: спец. символы")
    public void validationFieldMonthSpecialSymbolsValue() {
        CardInfo invalidMonth = new CardInfo("", generateSymbolString(2), "", "", "");

        PaymentPage paymentPage = tourPage.payByCard();
        paymentPage.fillCardData(invalidMonth);
        SelenideElement invalidMsg = paymentPage.getInvalidMsgMonth();

        invalidMsg
                .shouldBe(visible)
                .shouldHave(text(wrongFormatMsg));
    }

    @Test
    @DisplayName("Валидация данных в поле “Месяц”: буквы")
    public void validationFieldMonthLettersValue() {
        CardInfo invalidMonth = new CardInfo("", generateName(), "", "", "");

        PaymentPage paymentPage = tourPage.payByCard();
        paymentPage.fillCardData(invalidMonth);
        SelenideElement invalidMsg = paymentPage.getInvalidMsgMonth();

        invalidMsg
                .shouldBe(visible)
                .shouldHave(text(wrongFormatMsg));
    }

    @Test
    @DisplayName("Валидация данных в поле “Год”: текущий год")
    public void validationFieldYearCurrent() {
        CardInfo currentYear = new CardInfo("", "", generateYear(0), "", "");

        PaymentPage paymentPage = tourPage.payByCard();
        paymentPage.fillCardData(currentYear);
        SelenideElement invalidMsg = paymentPage.getInvalidMsgYear();

        invalidMsg.shouldNot(exist);
    }

    @Test
    @DisplayName("Валидация данных в поле “Год”: предыдущий год")
    public void validationFieldYearPrev() {
        CardInfo prevYear = new CardInfo(generateNumber(cardNumberCount), generateMonth(), generateYear(-1), generateOwner(), generateNumber(cvcCount));

        PaymentPage paymentPage = tourPage.payByCard();
        paymentPage.fillCardData(prevYear);
        SelenideElement invalidMsg = paymentPage.getInvalidMsgYear();

        invalidMsg
                .shouldBe(visible)
                .shouldHave(text(expiredCard));
    }

    @Test
    @DisplayName("Валидация данных в поле “Год”: плюс 5 лет к текущему году")
    public void validationFieldYearPlus5() {
        CardInfo plus5Year = new CardInfo(generateNumber(cardNumberCount), generateMonth(), generateYear(5), generateOwner(), generateNumber(cvcCount));

        PaymentPage paymentPage = tourPage.payByCard();
        paymentPage.fillCardData(plus5Year);
        SelenideElement invalidMsg = paymentPage.getInvalidMsgYear();

        invalidMsg.shouldNot(exist);
    }

    @Test
    @DisplayName("Валидация данных в поле “Год”: плюс 6 лет к текущему году")
    public void validationFieldYearPlus6() {
        CardInfo plus6Year = new CardInfo(generateNumber(cardNumberCount), generateMonth(), generateYear(6), generateOwner(), generateNumber(cvcCount));

        PaymentPage paymentPage = tourPage.payByCard();
        paymentPage.fillCardData(plus6Year);
        SelenideElement invalidMsg = paymentPage.getInvalidMsgYear();

        invalidMsg
                .shouldBe(visible)
                .shouldHave(text(wrongDate));
    }

    @Test
    @DisplayName("Валидация данных в поле “Год”: спец. символы")
    public void validationFieldYearSpecialSymbol() {
        CardInfo specialSymbolsYear = new CardInfo(generateNumber(cardNumberCount), generateMonth(), generateSymbolString(2), generateOwner(), generateNumber(cvcCount));

        PaymentPage paymentPage = tourPage.payByCard();
        paymentPage.fillCardData(specialSymbolsYear);
        SelenideElement invalidMsg = paymentPage.getInvalidMsgYear();
        invalidMsg
                .shouldBe(visible)
                .shouldHave(text(wrongFormatMsg));
    }

    @Test
    @DisplayName("Валидация данных в поле “Год”: буквы")
    public void validationFieldYearLetters() {
        CardInfo lettersYear = new CardInfo(generateNumber(cardNumberCount), generateMonth(), generateLastName(), generateOwner(), generateNumber(cvcCount));

        PaymentPage paymentPage = tourPage.payByCard();
        paymentPage.fillCardData(lettersYear);
        SelenideElement invalidMsg = paymentPage.getInvalidMsgYear();

        invalidMsg
                .shouldBe(visible)
                .shouldHave(text(wrongFormatMsg));
    }

    @Test
    @DisplayName("Валидация данных в поле “Владелец”: имя + фамилия латиница")
    public void validationFieldOwnerLatin() {
        CardInfo latinOwner = new CardInfo(generateNumber(cardNumberCount), generateMonth(), generateYear(shift), generateOwner(), generateNumber(cvcCount));

        PaymentPage paymentPage = tourPage.payByCard();
        paymentPage.fillCardData(latinOwner);
        SelenideElement invalidMsg = paymentPage.getInvalidMsgOwner();

        invalidMsg.shouldNot(exist);
    }

    @Test
    @DisplayName("Валидация данных в поле “Владелец”: дефис в имени")
    public void validationFieldOwnerHyphenInName() {
        String hyphenInName = generateName() + "-" + generateName() + " " + generateLastName();
        CardInfo hyphenOwner = new CardInfo(generateNumber(cardNumberCount), generateMonth(), generateYear(shift), hyphenInName, generateNumber(cvcCount));

        PaymentPage paymentPage = tourPage.payByCard();
        paymentPage.fillCardData(hyphenOwner);
        SelenideElement invalidMsg = paymentPage.getInvalidMsgOwner();

        invalidMsg.shouldNot(exist);
    }

    @Test
    @DisplayName("Валидация данных в поле “Владелец”: дефис в фамилии")
    public void validationFieldOwnerHyphenInLastName() {
        String hyphenInLastName = generateName() + " " + generateLastName() + "-" + generateLastName();
        CardInfo hyphenOwner = new CardInfo(generateNumber(cardNumberCount), generateMonth(), generateYear(shift), hyphenInLastName, generateNumber(cvcCount));

        PaymentPage paymentPage = tourPage.payByCard();
        paymentPage.fillCardData(hyphenOwner);
        SelenideElement invalidMsg = paymentPage.getInvalidMsgOwner();

        invalidMsg.shouldNot(exist);
    }

    @Test
    @DisplayName("Валидация данных в поле “Владелец”: upper case")
    public void validationFieldOwnerUpperCase() {
        CardInfo upperCaseOwner = new CardInfo(generateNumber(cardNumberCount), generateMonth(), generateYear(shift), generateOwner().toUpperCase(), generateNumber(cvcCount));

        PaymentPage paymentPage = tourPage.payByCard();
        paymentPage.fillCardData(upperCaseOwner);
        SelenideElement invalidMsg = paymentPage.getInvalidMsgOwner();

        invalidMsg.shouldNot(exist);
    }

    @Test
    @DisplayName("Валидация данных в поле “Владелец”: только имя")
    public void validationFieldOwnerOnlyName() {
        CardInfo onlyNameOwner = new CardInfo(generateNumber(cardNumberCount), generateMonth(), generateYear(shift), generateName(), generateNumber(cvcCount));

        PaymentPage paymentPage = tourPage.payByCard();
        paymentPage.fillCardData(onlyNameOwner);
        SelenideElement invalidMsg = paymentPage.getInvalidMsgOwner();

        invalidMsg
                .shouldBe(visible)
                .shouldHave(text(wrongFormatMsg));
    }

    @Test
    @DisplayName("Валидация данных в поле “Владелец”: только фамилия")
    public void validationFieldOwnerOnlyLastName() {
        CardInfo onlyLastNameOwner = new CardInfo(generateNumber(cardNumberCount), generateMonth(), generateYear(shift), generateLastName(), generateNumber(cvcCount));

        PaymentPage paymentPage = tourPage.payByCard();
        paymentPage.fillCardData(onlyLastNameOwner);
        SelenideElement invalidMsg = paymentPage.getInvalidMsgOwner();

        invalidMsg
                .shouldBe(visible)
                .shouldHave(text(wrongFormatMsg));
    }

    @Test
    @DisplayName("Валидация данных в поле “Владелец”: лишний пробел в начале")
    public void validationFieldOwnerOnlyStartExtraSpace() {
        CardInfo startExtraSpaceOwner = new CardInfo(generateNumber(cardNumberCount), generateMonth(), generateYear(shift), " " + generateOwner(), generateNumber(cvcCount));

        PaymentPage paymentPage = tourPage.payByCard();
        paymentPage.fillCardData(startExtraSpaceOwner);
        SelenideElement invalidMsg = paymentPage.getInvalidMsgOwner();

        invalidMsg.shouldNot(exist);
    }

    @Test
    @DisplayName("Валидация данных в поле “Владелец”: лишний пробел в середине")
    public void validationFieldOwnerOnlyStartMiddleExtraSpace() {
        CardInfo middleExtraSpaceOwner = new CardInfo(generateNumber(cardNumberCount), generateMonth(), generateYear(shift), generateName() + " " + " " + generateLastName(), generateNumber(cvcCount));

        PaymentPage paymentPage = tourPage.payByCard();
        paymentPage.fillCardData(middleExtraSpaceOwner);
        SelenideElement invalidMsg = paymentPage.getInvalidMsgOwner();

        invalidMsg.shouldNot(exist);
    }

    @Test
    @DisplayName("Валидация данных в поле “Владелец”: лишний пробел в конце")
    public void validationFieldOwnerOnlyStartEndExtraSpace() {
        CardInfo endExtraSpaceOwner = new CardInfo(generateNumber(cardNumberCount), generateMonth(), generateYear(shift), generateOwner() + " ", generateNumber(cvcCount));

        PaymentPage paymentPage = tourPage.payByCard();
        paymentPage.fillCardData(endExtraSpaceOwner);
        SelenideElement invalidMsg = paymentPage.getInvalidMsgOwner();

        invalidMsg.shouldNot(exist);
    }

    @Test
    @DisplayName("Валидация данных в поле “Владелец”: кириллица")
    public void validationFieldOwnerCyrillic() {
        CardInfo cyrillicOwner = new CardInfo(generateNumber(cardNumberCount), generateMonth(), generateYear(shift), generateCyrillicOwner(), generateNumber(cvcCount));

        PaymentPage paymentPage = tourPage.payByCard();
        paymentPage.fillCardData(cyrillicOwner);
        SelenideElement invalidMsg = paymentPage.getInvalidMsgOwner();

        invalidMsg
                .shouldBe(visible)
                .shouldHave(text(wrongFormatMsg));
    }

    @Test
    @DisplayName("Валидация данных в поле “Владелец”: спец. символы")
    public void validationFieldSpecialSymbol() {
        CardInfo specialSymbolOwner = new CardInfo(generateNumber(cardNumberCount), generateMonth(), generateYear(shift), generateSymbolString(5), generateNumber(cvcCount));

        PaymentPage paymentPage = tourPage.payByCard();
        paymentPage.fillCardData(specialSymbolOwner);
        SelenideElement invalidMsg = paymentPage.getInvalidMsgOwner();

        invalidMsg
                .shouldBe(visible)
                .shouldHave(text(wrongFormatMsg));
    }

    @Test
    @DisplayName("Валидация данных в поле “Владелец”: цифры")
    public void validationFieldDigits() {
        CardInfo digitsOwner = new CardInfo(generateNumber(cardNumberCount), generateMonth(), generateYear(shift), generateNumber(5), generateNumber(cvcCount));

        PaymentPage paymentPage = tourPage.payByCard();
        paymentPage.fillCardData(digitsOwner);
        SelenideElement invalidMsg = paymentPage.getInvalidMsgOwner();

        invalidMsg
                .shouldBe(visible)
                .shouldHave(text(wrongFormatMsg));
    }

    @Test
    @DisplayName("Валидация данных в поле “CVC/CVV”: 3 цифры")
    public void validationFieldCVCValidValue() {
        CardInfo validCVC = new CardInfo("", "", "", "", generateNumber(cvcCount));

        PaymentPage paymentPage = tourPage.payByCard();
        paymentPage.fillCardData(validCVC);
        SelenideElement invalidMsg = paymentPage.getInvalidMsgCVC();

        invalidMsg.shouldNot(exist);
    }

    @Test
    @DisplayName("Валидация данных в поле “CVC/CVV”: 2 цифры")
    public void validationFieldCVC2digits() {
        CardInfo lessDigitsCVC = new CardInfo("", "", "", "", generateNumber(cvcCount - 1));


        PaymentPage paymentPage = tourPage.payByCard();
        paymentPage.fillCardData(lessDigitsCVC);
        SelenideElement invalidMsg = paymentPage.getInvalidMsgCVC();

        invalidMsg
                .shouldBe(visible)
                .shouldHave(text(wrongFormatMsg));
    }

    @Test
    @DisplayName("Валидация данных в поле “CVC/CVV”: 4 цифры")
    public void validationFieldCVC4digits() {
        CardInfo moreDigitsCVC = new CardInfo("", "", "", "", generateNumber(cvcCount + 1));

        PaymentPage paymentPage = tourPage.payByCard();
        paymentPage.fillCardData(moreDigitsCVC);
        SelenideElement invalidMsg = paymentPage.getInvalidMsgCVC();

        //поле не принимает больше необходимого количества цифр
        assertEquals(cvcCount, paymentPage.getFieldValueCVC().length());
        invalidMsg.shouldNot(exist);
    }

    @Test
    @DisplayName("Валидация данных в поле “CVC/CVV”: спец. символы")
    public void validationFieldCVCSpecialSymbols() {
        CardInfo specialSymbolsCVC = new CardInfo("", "", "", "", generateSymbolString(cvcCount));

        PaymentPage paymentPage = tourPage.payByCard();
        paymentPage.fillCardData(specialSymbolsCVC);
        SelenideElement invalidMsg = paymentPage.getInvalidMsgCVC();

        //поле не принимает ничего кроме цифр
        assertEquals(0, paymentPage.getFieldValueCVC().length());
        invalidMsg
                .shouldBe(visible)
                .shouldHave(text(wrongFormatMsg));
    }

    @Test
    @DisplayName("Валидация данных в поле “CVC/CVV”: буквы")
    public void validationFieldCVCLetters() {
        CardInfo specialSymbolsCVC = new CardInfo("", "", "", "", generateName());

        PaymentPage paymentPage = tourPage.payByCard();
        paymentPage.fillCardData(specialSymbolsCVC);
        SelenideElement invalidMsg = paymentPage.getInvalidMsgCVC();

        //поле не принимает ничего кроме цифр
        assertEquals(0, paymentPage.getFieldValueCVC().length());
        invalidMsg
                .shouldBe(visible)
                .shouldHave(text(wrongFormatMsg));
    }

    // Тесты на форму кредита
    @Test
    @DisplayName("Оплата тура в кредит со статусом «APPROVED»")
    public void creditForTourByCardStatusApproved() {
        CardInfo approvedCard = generateValidCardInfo(0);

        PaymentPage paymentPage = tourPage.payByCredit();
        String notificationMsg = paymentPage.validPayment(approvedCard);

        assertTrue(notificationMsg.contains(approvedCardMsg));
    }

    @Test
    @DisplayName("Оплата тура в кредит со статусом «DECLINED»")
    public void creditForTourByCardStatusDeclined() {
        CardInfo declinedCard = generateValidCardInfo(1);

        PaymentPage paymentPage = tourPage.payByCredit();
        String notificationMsg = paymentPage.validPayment(declinedCard);

        assertTrue(notificationMsg.contains(declinedCardMsg));
        //отображается уведомление успешно вместо ошибки
    }

    @Test
    @DisplayName("Оплата тура в кредит с незарегистрированным номером карты")
    public void creditForTourByNotRegisteredCard() {
        CardInfo unregisteredCard = new CardInfo(generateNumber(cardNumberCount), generateMonth(), generateYear(shift), generateOwner(), generateNumber(cvcCount));

        PaymentPage paymentPage = tourPage.payByCredit();
        String notificationMsg = paymentPage.validPayment(unregisteredCard);

        assertTrue(notificationMsg.contains(declinedCardMsg));
        //отображаются оба уведомления поверх ошибка, снизу успешно, поэтому ловиться первый элемент успешно
    }

    @Test
    @DisplayName("Отправка в заявке на кредит пустой формы")
    public void sendCreditEmptyForm() {
        PaymentPage paymentPage = tourPage.payByCredit();
        paymentPage.sendForm();
        ElementsCollection invalidMsgCollection = paymentPage.getCollectionInvalidMsg();

        assertEquals(5, invalidMsgCollection.size());
    }

    @Test
    @DisplayName("Валидация данных формы Кредит в поле «Номер карты»: 16 цифр»")
    public void validationCreditFieldCardNumber16Digits() {
        CardInfo validCardNumber = new CardInfo(generateNumber(16), "", "", "", "");

        PaymentPage paymentPage = tourPage.payByCredit();
        paymentPage.fillCardData(validCardNumber);
        SelenideElement invalidMsg = paymentPage.getInvalidMsgCardNumber();

        invalidMsg.shouldNot(exist);
    }

    @Test
    @DisplayName("Валидация данных формы Кредит в поле «Номер карты»: 15 цифр»")
    public void validationCreditFieldCardNumber15Digits() {
        CardInfo invalidCardNumber = new CardInfo(generateNumber(cardNumberCount - 1), "", "", "", "");

        PaymentPage paymentPage = tourPage.payByCredit();
        paymentPage.fillCardData(invalidCardNumber);
        SelenideElement invalidMsg = paymentPage.getInvalidMsgCardNumber();

        invalidMsg
                .shouldBe(visible)
                .shouldHave(text(wrongFormatMsg));
    }

    @Test
    @DisplayName("Валидация данных формы Кредит в поле «Номер карты»: 17 цифр»")
    public void validationCreditFieldCardNumber17Digits() {
        CardInfo invalidCardNumber = new CardInfo(generateNumber(cardNumberCount + 1), "", "", "", "");

        PaymentPage paymentPage = tourPage.payByCredit();
        paymentPage.fillCardData(invalidCardNumber);
        SelenideElement invalidMsg = paymentPage.getInvalidMsgCardNumber();

        //добавляем к количеству цифр в номере карты 3 пробела разделителя
        assertEquals(cardNumberCount + 3, paymentPage.getFieldValueCardNumber().length());
        invalidMsg.shouldNot(exist);
    }

    @Test
    @DisplayName("Валидация данных формы Кредит в поле «Номер карты»: спец. символы")
    public void validationCreditFieldCardNumberSpecialSymbols() {
        String cardNumber = generateSymbolString(cardNumberCount);
        CardInfo invalidCardNumber = new CardInfo(cardNumber, "", "", "", "");

        PaymentPage paymentPage = tourPage.payByCredit();
        paymentPage.fillCardData(invalidCardNumber);
        SelenideElement invalidMsg = paymentPage.getInvalidMsgCardNumber();


        assertEquals(0, paymentPage.getFieldValueCardNumber().length());
        invalidMsg
                .shouldBe(visible)
                .shouldHave(text(wrongFormatMsg));
    }

    @Test
    @DisplayName("Валидация данных формы Кредит в поле «Номер карты»: буквы")
    public void validationCreditFieldCardNumberLetters() {
        String cardNumber = generateLastName();
        CardInfo invalidCardNumber = new CardInfo(cardNumber, "", "", "", "");

        PaymentPage paymentPage = tourPage.payByCredit();
        paymentPage.fillCardData(invalidCardNumber);
        SelenideElement invalidMsg = paymentPage.getInvalidMsgCardNumber();


        assertEquals(0, paymentPage.getFieldValueCardNumber().length());
        invalidMsg
                .shouldBe(visible)
                .shouldHave(text(wrongFormatMsg));
    }

    @Test
    @DisplayName("Валидация данных формы Кредит в поле “Месяц”: число от 1 до 12 в двузначном формате")
    public void validationCreditFieldMonthValidValue() {
        CardInfo validMonth = new CardInfo("", generateMonth(), "", "", "");

        PaymentPage paymentPage = tourPage.payByCredit();
        paymentPage.fillCardData(validMonth);
        SelenideElement invalidMsg = paymentPage.getInvalidMsgMonth();

        invalidMsg.shouldNot(exist);

    }

    @Test
    @DisplayName("Валидация данных формы Кредит в поле “Месяц”: число 00")
    public void validationCreditFieldMonth00Value() {
        CardInfo invalidMonth = new CardInfo("", "00", "", "", "");

        PaymentPage paymentPage = tourPage.payByCredit();
        paymentPage.fillCardData(invalidMonth);
        SelenideElement invalidMsg = paymentPage.getInvalidMsgMonth();

        invalidMsg
                .shouldBe(visible)
                .shouldHave(text(wrongFormatMsg));
    }

    @Test
    @DisplayName("Валидация данных формы Кредит в поле “Месяц”: число 13")
    public void validationCreditFieldMonth13Value() {
        CardInfo invalidMonth = new CardInfo(generateNumber(cardNumberCount), String.valueOf(monthCount + 1), generateYear(shift), generateOwner(), generateNumber(cvcCount));

        PaymentPage paymentPage = tourPage.payByCredit();
        paymentPage.fillCardData(invalidMonth);
        SelenideElement invalidMsg = paymentPage.getInvalidMsgMonth();

        invalidMsg
                .shouldBe(visible)
                .shouldHave(text(wrongDate));
    }

    @Test
    @DisplayName("Валидация данных формы Кредит в поле “Месяц”: цифры от 1 до 9")
    public void validationCreditFieldMonthDigitsValue() {
        CardInfo invalidMonth = new CardInfo(generateNumber(cardNumberCount), generateNumber(1), generateYear(shift), generateOwner(), generateNumber(cvcCount));

        PaymentPage paymentPage = tourPage.payByCredit();
        paymentPage.fillCardData(invalidMonth);
        SelenideElement invalidMsg = paymentPage.getInvalidMsgMonth();

        invalidMsg
                .shouldBe(visible)
                .shouldHave(text(wrongFormatMsg));
    }

    @Test
    @DisplayName("Валидация данных формы Кредит в поле “Месяц”: спец. символы")
    public void validationCreditFieldMonthSpecialSymbolsValue() {
        CardInfo invalidMonth = new CardInfo("", generateSymbolString(2), "", "", "");

        PaymentPage paymentPage = tourPage.payByCredit();
        paymentPage.fillCardData(invalidMonth);
        SelenideElement invalidMsg = paymentPage.getInvalidMsgMonth();

        invalidMsg
                .shouldBe(visible)
                .shouldHave(text(wrongFormatMsg));
    }

    @Test
    @DisplayName("Валидация данных формы Кредит в поле “Месяц”: буквы")
    public void validationCreditFieldMonthLettersValue() {
        CardInfo invalidMonth = new CardInfo("", generateName(), "", "", "");

        PaymentPage paymentPage = tourPage.payByCredit();
        paymentPage.fillCardData(invalidMonth);
        SelenideElement invalidMsg = paymentPage.getInvalidMsgMonth();

        invalidMsg
                .shouldBe(visible)
                .shouldHave(text(wrongFormatMsg));
    }

    @Test
    @DisplayName("Валидация данных формы Кредит в поле “Год”: текущий год")
    public void validationCreditFieldYearCurrent() {
        CardInfo currentYear = new CardInfo("", "", generateYear(0), "", "");

        PaymentPage paymentPage = tourPage.payByCredit();
        paymentPage.fillCardData(currentYear);
        SelenideElement invalidMsg = paymentPage.getInvalidMsgYear();

        invalidMsg.shouldNot(exist);
    }

    @Test
    @DisplayName("Валидация данных формы Кредит в поле “Год”: предыдущий год")
    public void validationCreditFieldYearPrev() {
        CardInfo prevYear = new CardInfo(generateNumber(cardNumberCount), generateMonth(), generateYear(-1), generateOwner(), generateNumber(cvcCount));

        PaymentPage paymentPage = tourPage.payByCredit();
        paymentPage.fillCardData(prevYear);
        SelenideElement invalidMsg = paymentPage.getInvalidMsgYear();

        invalidMsg
                .shouldBe(visible)
                .shouldHave(text(expiredCard));
    }

    @Test
    @DisplayName("Валидация данных формы Кредит в поле “Год”: плюс 5 лет к текущему году")
    public void validationCreditFieldYearPlus5() {
        CardInfo plus5Year = new CardInfo(generateNumber(cardNumberCount), generateMonth(), generateYear(5), generateOwner(), generateNumber(cvcCount));

        PaymentPage paymentPage = tourPage.payByCredit();
        paymentPage.fillCardData(plus5Year);
        SelenideElement invalidMsg = paymentPage.getInvalidMsgYear();

        invalidMsg.shouldNot(exist);
    }

    @Test
    @DisplayName("Валидация данных формы Кредит в поле “Год”: плюс 6 лет к текущему году")
    public void validationCreditFieldYearPlus6() {
        CardInfo plus6Year = new CardInfo(generateNumber(cardNumberCount), generateMonth(), generateYear(6), generateOwner(), generateNumber(cvcCount));

        PaymentPage paymentPage = tourPage.payByCredit();
        paymentPage.fillCardData(plus6Year);
        SelenideElement invalidMsg = paymentPage.getInvalidMsgYear();

        invalidMsg
                .shouldBe(visible)
                .shouldHave(text(wrongDate));
    }

    @Test
    @DisplayName("Валидация данных формы Кредит в поле “Год”: спец. символы")
    public void validationCreditFieldYearSpecialSymbol() {
        CardInfo specialSymbolsYear = new CardInfo(generateNumber(cardNumberCount), generateMonth(), generateSymbolString(2), generateOwner(), generateNumber(cvcCount));

        PaymentPage paymentPage = tourPage.payByCredit();
        paymentPage.fillCardData(specialSymbolsYear);
        SelenideElement invalidMsg = paymentPage.getInvalidMsgYear();

        invalidMsg
                .shouldBe(visible)
                .shouldHave(text(wrongFormatMsg));
    }

    @Test
    @DisplayName("Валидация данных формы Кредит в поле “Год”: буквы")
    public void validationCreditFieldYearLetters() {
        CardInfo lettersYear = new CardInfo(generateNumber(cardNumberCount), generateMonth(), generateLastName(), generateOwner(), generateNumber(cvcCount));

        PaymentPage paymentPage = tourPage.payByCredit();
        paymentPage.fillCardData(lettersYear);
        SelenideElement invalidMsg = paymentPage.getInvalidMsgYear();

        invalidMsg
                .shouldBe(visible)
                .shouldHave(text(wrongFormatMsg));
    }

    @Test
    @DisplayName("Валидация данных формы Кредит в поле “Владелец”: имя + фамилия латиница")
    public void validationCreditFieldOwnerLatin() {
        CardInfo latinOwner = new CardInfo(generateNumber(cardNumberCount), generateMonth(), generateYear(shift), generateOwner(), generateNumber(cvcCount));

        PaymentPage paymentPage = tourPage.payByCredit();
        paymentPage.fillCardData(latinOwner);
        SelenideElement invalidMsg = paymentPage.getInvalidMsgOwner();

        invalidMsg.shouldNot(exist);
    }

    @Test
    @DisplayName("Валидация данных формы Кредит в поле “Владелец”: дефис в имени")
    public void validationCreditFieldOwnerHyphenInName() {
        String hyphenInName = generateName() + "-" + generateName() + " " + generateLastName();
        CardInfo hyphenOwner = new CardInfo(generateNumber(cardNumberCount), generateMonth(), generateYear(shift), hyphenInName, generateNumber(cvcCount));

        PaymentPage paymentPage = tourPage.payByCredit();
        paymentPage.fillCardData(hyphenOwner);
        SelenideElement invalidMsg = paymentPage.getInvalidMsgOwner();

        invalidMsg.shouldNot(exist);
    }

    @Test
    @DisplayName("Валидация данных формы Кредит в поле “Владелец”: дефис в фамилии")
    public void validationCreditFieldOwnerHyphenInLastName() {
        String hyphenInLastName = generateName() + " " + generateLastName() + "-" + generateLastName();
        CardInfo hyphenOwner = new CardInfo(generateNumber(cardNumberCount), generateMonth(), generateYear(shift), hyphenInLastName, generateNumber(cvcCount));

        PaymentPage paymentPage = tourPage.payByCredit();
        paymentPage.fillCardData(hyphenOwner);
        SelenideElement invalidMsg = paymentPage.getInvalidMsgOwner();

        invalidMsg.shouldNot(exist);
    }

    @Test
    @DisplayName("Валидация данных формы Кредит в поле “Владелец”: upper case")
    public void validationCreditFieldOwnerUpperCase() {
        CardInfo upperCaseOwner = new CardInfo(generateNumber(cardNumberCount), generateMonth(), generateYear(shift), generateOwner().toUpperCase(), generateNumber(cvcCount));

        PaymentPage paymentPage = tourPage.payByCredit();
        paymentPage.fillCardData(upperCaseOwner);
        SelenideElement invalidMsg = paymentPage.getInvalidMsgOwner();

        invalidMsg.shouldNot(exist);
    }

    @Test
    @DisplayName("Валидация данных формы Кредит в поле “Владелец”: только имя")
    public void validationCreditFieldOwnerOnlyName() {
        CardInfo onlyNameOwner = new CardInfo(generateNumber(cardNumberCount), generateMonth(), generateYear(shift), generateName(), generateNumber(cvcCount));

        PaymentPage paymentPage = tourPage.payByCredit();
        paymentPage.fillCardData(onlyNameOwner);
        SelenideElement invalidMsg = paymentPage.getInvalidMsgOwner();

        invalidMsg
                .shouldBe(visible)
                .shouldHave(text(wrongFormatMsg));
    }

    @Test
    @DisplayName("Валидация данных формы Кредит в поле “Владелец”: только фамилия")
    public void validationCreditFieldOwnerOnlyLastName() {
        CardInfo onlyLastNameOwner = new CardInfo(generateNumber(cardNumberCount), generateMonth(), generateYear(shift), generateLastName(), generateNumber(cvcCount));

        PaymentPage paymentPage = tourPage.payByCredit();
        paymentPage.fillCardData(onlyLastNameOwner);
        SelenideElement invalidMsg = paymentPage.getInvalidMsgOwner();

        invalidMsg
                .shouldBe(visible)
                .shouldHave(text(wrongFormatMsg));
    }

    @Test
    @DisplayName("Валидация данных формы Кредит в поле “Владелец”: лишний пробел в начале")
    public void validationCreditFieldOwnerOnlyStartExtraSpace() {
        CardInfo startExtraSpaceOwner = new CardInfo(generateNumber(cardNumberCount), generateMonth(), generateYear(shift), " " + generateOwner(), generateNumber(cvcCount));

        PaymentPage paymentPage = tourPage.payByCredit();
        paymentPage.fillCardData(startExtraSpaceOwner);
        SelenideElement invalidMsg = paymentPage.getInvalidMsgOwner();

        invalidMsg.shouldNot(exist);
    }

    @Test
    @DisplayName("Валидация данных формы Кредит в поле “Владелец”: лишний пробел в середине")
    public void validationCreditFieldOwnerOnlyStartMiddleExtraSpace() {
        CardInfo middleExtraSpaceOwner = new CardInfo(generateNumber(cardNumberCount), generateMonth(), generateYear(shift), generateName() + " " + " " + generateLastName(), generateNumber(cvcCount));

        PaymentPage paymentPage = tourPage.payByCredit();
        paymentPage.fillCardData(middleExtraSpaceOwner);
        SelenideElement invalidMsg = paymentPage.getInvalidMsgOwner();

        invalidMsg.shouldNot(exist);
    }

    @Test
    @DisplayName("Валидация данных формы Кредит в поле “Владелец”: лишний пробел в конце")
    public void validationCreditFieldOwnerOnlyStartEndExtraSpace() {
        CardInfo endExtraSpaceOwner = new CardInfo(generateNumber(cardNumberCount), generateMonth(), generateYear(shift), generateOwner() + " ", generateNumber(cvcCount));

        PaymentPage paymentPage = tourPage.payByCredit();
        paymentPage.fillCardData(endExtraSpaceOwner);
        SelenideElement invalidMsg = paymentPage.getInvalidMsgOwner();

        invalidMsg.shouldNot(exist);
    }

    @Test
    @DisplayName("Валидация данных формы Кредит в поле “Владелец”: кириллица")
    public void validationCreditFieldOwnerCyrillic() {
        CardInfo cyrillicOwner = new CardInfo(generateNumber(cardNumberCount), generateMonth(), generateYear(shift), generateCyrillicOwner(), generateNumber(cvcCount));

        PaymentPage paymentPage = tourPage.payByCredit();
        paymentPage.fillCardData(cyrillicOwner);
        SelenideElement invalidMsg = paymentPage.getInvalidMsgOwner();

        invalidMsg
                .shouldBe(visible)
                .shouldHave(text(wrongFormatMsg));
    }

    @Test
    @DisplayName("Валидация данных формы Кредит в поле “Владелец”: спец. символы")
    public void validationCreditFieldSpecialSymbol() {
        CardInfo specialSymbolOwner = new CardInfo(generateNumber(cardNumberCount), generateMonth(), generateYear(shift), generateSymbolString(5), generateNumber(cvcCount));

        PaymentPage paymentPage = tourPage.payByCredit();
        paymentPage.fillCardData(specialSymbolOwner);
        SelenideElement invalidMsg = paymentPage.getInvalidMsgOwner();

        invalidMsg
                .shouldBe(visible)
                .shouldHave(text(wrongFormatMsg));
    }

    @Test
    @DisplayName("Валидация данных формы Кредит в поле “Владелец”: цифры")
    public void validationCreditFieldDigits() {
        CardInfo digitsOwner = new CardInfo(generateNumber(cardNumberCount), generateMonth(), generateYear(shift), generateNumber(5), generateNumber(cvcCount));

        PaymentPage paymentPage = tourPage.payByCredit();
        paymentPage.fillCardData(digitsOwner);
        SelenideElement invalidMsg = paymentPage.getInvalidMsgOwner();

        invalidMsg
                .shouldBe(visible)
                .shouldHave(text(wrongFormatMsg));
    }

    @Test
    @DisplayName("Валидация данных формы Кредит в поле “CVC/CVV”: 3 цифры")
    public void validationCreditFieldCVCValidValue() {
        CardInfo validCVC = new CardInfo("", "", "", "", generateNumber(cvcCount));

        PaymentPage paymentPage = tourPage.payByCredit();
        paymentPage.fillCardData(validCVC);
        SelenideElement invalidMsg = paymentPage.getInvalidMsgCVC();

        invalidMsg.shouldNot(exist);
    }

    @Test
    @DisplayName("Валидация данных формы Кредит в поле “CVC/CVV”: 2 цифры")
    public void validationCreditFieldCVC2digits() {
        CardInfo lessDigitsCVC = new CardInfo("", "", "", "", generateNumber(cvcCount - 1));


        PaymentPage paymentPage = tourPage.payByCredit();
        paymentPage.fillCardData(lessDigitsCVC);
        SelenideElement invalidMsg = paymentPage.getInvalidMsgCVC();

        invalidMsg
                .shouldBe(visible)
                .shouldHave(text(wrongFormatMsg));
    }

    @Test
    @DisplayName("Валидация данных формы Кредит в поле “CVC/CVV”: 4 цифры")
    public void validationCreditFieldCVC4digits() {
        CardInfo moreDigitsCVC = new CardInfo("", "", "", "", generateNumber(cvcCount + 1));

        PaymentPage paymentPage = tourPage.payByCredit();
        paymentPage.fillCardData(moreDigitsCVC);
        SelenideElement invalidMsg = paymentPage.getInvalidMsgCVC();

        //поле не принимает больше необходимого количества цифр
        assertEquals(cvcCount, paymentPage.getFieldValueCVC().length());
        invalidMsg.shouldNot(exist);
    }

    @Test
    @DisplayName("Валидация данных формы Кредит в поле “CVC/CVV”: спец. символы")
    public void validationCreditFieldCVCSpecialSymbols() {
        CardInfo specialSymbolsCVC = new CardInfo("", "", "", "", generateSymbolString(cvcCount));

        PaymentPage paymentPage = tourPage.payByCredit();
        paymentPage.fillCardData(specialSymbolsCVC);
        SelenideElement invalidMsg = paymentPage.getInvalidMsgCVC();

        //поле не принимает ничего кроме цифр
        assertEquals(0, paymentPage.getFieldValueCVC().length());
        invalidMsg
                .shouldBe(visible)
                .shouldHave(text(wrongFormatMsg));
    }

    @Test
    @DisplayName("Валидация данных формы Кредит в поле “CVC/CVV”: буквы")
    public void validationCreditFieldCVCLetters() {
        CardInfo specialSymbolsCVC = new CardInfo("", "", "", "", generateName());

        PaymentPage paymentPage = tourPage.payByCredit();
        paymentPage.fillCardData(specialSymbolsCVC);
        SelenideElement invalidMsg = paymentPage.getInvalidMsgCVC();

        //поле не принимает ничего кроме цифр
        assertEquals(0, paymentPage.getFieldValueCVC().length());
        invalidMsg
                .shouldBe(visible)
                .shouldHave(text(wrongFormatMsg));
    }
}