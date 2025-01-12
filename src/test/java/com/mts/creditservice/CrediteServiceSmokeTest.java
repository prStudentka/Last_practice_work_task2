package com.mts.creditservice;

import com.mts.creditservice.core.UserOrdersData;
import com.mts.creditservice.endpoints.*;

import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.DisabledIf;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.ArrayList;

import java.util.Iterator;
import java.util.stream.Stream;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CrediteServiceSmokeTest extends BaseTest {
    private static ArrayList<UserOrdersData> ordersUser;

    @BeforeAll
    public static void setUp() {
        ordersUser = new ArrayList<>();
    }

    @AfterAll
    public static void tearDown() {
        ordersUser.clear();
    }

    @DisplayName("Позитивный тест создание нового пользователя")
    @Description("Проверка Post регистрации пользователя cо status_code")
    @Test
    public void testPostRegistration() throws IOException {
        Register newUser = new Register("Petr", "Lee","petrova57@mail.ru","1234");
        newUser.hasStatusCodeOkCheck();
        newUser.hasTokenCheck();
    }

    @Feature("Тарифы")
    @DisplayName("Позитивный тест получения списка тарифов")
    @Description("Проверка Get списка тарифов cо status_code 200")
    @Test
    public void testGetTariffStatusCode200() throws IOException {
        Tariffs tariff = new Tariffs(requestAuthSpecification);
        tariff.hasStatusCodeOkCheck();
        tariff.hasNotEmptyCheck();
        tariff.hasKeyCheck();
        tariff.hasSizeCheck();
        tariff.hasEntryCheck();
        tariff.hasItemsTypeCheck();
    }

    @Order(1)
    @DisplayName("Позитивный тест создания заявки на кредит")
    @Description("Проверка POST создания заявки cо status_code 200")
    @ParameterizedTest(name = "{index} => POST заявки на кредит (userId: {0}, tariffId: {1})")
    @CsvSource({
            "1, 2",
            "1, 3"
    })
    @Severity(SeverityLevel.CRITICAL)
    public void testPostOrderStatusCode200(long userId, long tariffId) throws IOException {
        ValidatableResponse response = PostOrder.checkOrder(requestAuthSpecification, userId, tariffId);
        PostOrder order = new PostOrder(response);
        order.hasStatusCodeOkCheck();
        order.hasNotEmptyCheck();
        order.hasKeyCheck();
        order.hasEntryCheck();

        UserOrdersData orderData = order.saveOrderId(userId);
        if (orderData != null) {
            ordersUser.add(orderData);
        }
    }

    @Order(3)
    @DisplayName("Позитивный тест получения статуса заявки")
    @Description("Проверка Get статуса заявки cо status_code 200")
    @DisabledIf("hasData")
    @ResourceLock(value = "ordersUser")
    @ParameterizedTest(name = "{index} => статус заявки")
    @MethodSource("argsProvider")
    @Severity(SeverityLevel.MINOR)
    public void testGetStatusOrderCode200(UserOrdersData order) throws IOException {
                StatusOrder status = new StatusOrder(requestAuthSpecification, order.getOrderId());
                status.hasStatusCodeOkCheck();
                status.hasNotEmptyCheck();
                status.hasKeyCheck();
                status.hasItemsStatusCheck();
    }


    @Order(4)
    @DisabledIf("hasData")
    @DisplayName("Позитивный тест удаления заявки на кредит")
    @Description("Проверка Delete заявки cо status_code 200")
    @ResourceLock(value = "ordersUser")
    @ParameterizedTest(name = "{index} => заявка на удаление")
    @MethodSource("argsProvider2")
    public void testDeleteOrderStatusCode200(UserOrdersData order) throws IOException {
         Response response = DeleteOrder.checkDeleteOrder(requestAuthSpecification, order.getUserId(), order.getOrderId());
         DeleteOrder deleteOrder = new DeleteOrder(response);
         deleteOrder.hasStatusCodeOkCheck();
    }

    static Stream<UserOrdersData> argsProvider() {
        return ordersUser.stream();
    }

    static Iterator<UserOrdersData> argsProvider2() {
        return ordersUser.iterator();
    }

    boolean hasData() {
       return ordersUser.isEmpty();
    }

    @Order(1)
    @Feature("Тарифы")
    @DisplayName("Позитивный тест создание нового тарифа")
    @Description("Проверка Post создания тарифа c проверкой status_code")
    @ParameterizedTest(name = "{index} => POST тарифа {0}")
    @CsvSource({
            "Samara, 0.5%",
            "Air, 37.2%%",
    })
    @Severity(SeverityLevel.BLOCKER)
    public void testPostAddTariff(String type, String interestRate) throws IOException {
        PostTariff newTariff = new PostTariff(requestAuthSpecification, type, interestRate);
        newTariff.hasStatusCodeOkCheck();
    }

    @Order(5)
    @Feature("Тарифы")
    @DisplayName("Позитивный тест удаления тарифа")
    @Description("Проверка Delete тарифа")
    @ParameterizedTest(name = "{index} => POST удаление тарифа {0}")
    @CsvSource({
            "Samara",
            "Air"
    })
    public void testPostDeleteTariff(String type) throws IOException {
        Tariffs tariffs = new Tariffs(requestAuthSpecification);
        tariffs.hasItemTypeCheck(type);
        long idTariff = tariffs.getIdByItemTypeCheck(type);
        DeleteTariff tariff = new DeleteTariff(requestAuthSpecification, idTariff);
        tariff.hasStatusCodeOkCheck();
    }
}
