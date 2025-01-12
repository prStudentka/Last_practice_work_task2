package com.mts.creditservice;

import com.mts.creditservice.endpoints.*;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.DisabledIf;

import org.junit.jupiter.api.parallel.ResourceLock;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertThrows;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CrediteServiceRegressionTest extends BaseTest {
    private String orderId;


    @Feature("Тарифы")
    @DisplayName("Негативный тест запроса Post создания тарифов")
    @Description("Повторное создание существующего тарифа 'Sale' c проверкой status code")
    @Test
    public void testPostTariffNegative() throws IOException {
        PostTariff newTariff = new PostTariff(requestAuthSpecification, "SALE", "10.2%");
        newTariff.hasStatusCodeNot200Check();
    }

    @DisplayName("Негативный тест заявки на кредит с граничными данными ")
    @Description("Проверка POST с граничными данными заявки и проверкой сообщения об ошибке")
    @ParameterizedTest(name = "{index} => POST заявки на кредит (userId: {0}, tariffId: {1})")
    @CsvSource({
            "1, 0, TARIFF_NOT_FOUND",
            "1000, 3, USER_NOT_FOUND",
            "1, 9223327036854775999, TARIFF_NOT_FOUND",
            "-1, 2, USER_NOT_FOUND",
            "0, 20, USER_NOT_FOUND",
    })
    public void testPostOrderStatusCode(long userId, long tariffId, String expectedMessage) throws IOException {
        ValidatableResponse response = PostOrder.checkOrder(requestAuthSpecification, userId, tariffId);
        PostOrder order = new PostOrder(response);
        order.hasStatusCodeBadCheck();
        order.hasBodyErrorCodeCheck(expectedMessage);
    }

    @Order(1)
    @DisplayName("Негативный тест заявки на кредит с повторением и получением ошибки LOAN_CONSIDERATION")
    @Description("Проверка повторяющейся POST заявки c проверкой status_code и получением ошибки LOAN_CONSIDERATION")
    @RepeatedTest(value=3, name = "{currentRepetition}/{totalRepetitions} => POST заявки на кредит (userId: 1, tariffId: 2)")
    public void testPostOrderRepeated(RepetitionInfo repetitionInfo) throws IOException {
        ValidatableResponse response = PostOrder.checkOrder(requestAuthSpecification, 1, 2);
        PostOrder order = new PostOrder(response);
        if (repetitionInfo.getCurrentRepetition() >= 2) {
            order.hasStatusCodeBadCheck();
            order.hasBodyErrorCodeCheck("LOAN_CONSIDERATION");
        } else {
            order.hasStatusCodeOkCheck();
            orderId = order.saveOrderId(1).getOrderId();
        }
    }

    @DisplayName("Негативный тест получения статуса заявки с некорректными данными")
    @Description("Проверка Get статуса заявки с некорректными данными и проверкой сообщения")
    @ParameterizedTest(name = "{index} => статус заявки {0}")
    @CsvSource(delimiter = ',', textBlock = """
         data with old orderId, 7822ca8b-d44a-4f92-8093-74187a392b5f, ORDER_NOT_FOUND
         data with zero orderId, 00000000-0000-0000-0000-000000000000, ORDER_NOT_FOUND
         data with letter orderId, aaAAaaAA-bbBB-ccCC-ddDD-eeeeeeeeeeef, ORDER_NOT_FOUND
    """)
    public void testGetStatusOrderCode(String desc, String orderId, String expectedMessage) throws IOException {
                StatusOrder status = new StatusOrder(requestAuthSpecification, orderId);
                status.hasStatusCodeBadCheck();
                status.hasBodyErrorCodeCheck(expectedMessage);
    }

    @Order(2)
    @DisplayName("Позитивный тест удаления заявки на кредит")
    @Description("Проверка Delete заявки (userId: 1)")
    @Test
    public void testDeleteOrderPositive() throws IOException {
        Response response = DeleteOrder.checkDeleteOrder(requestAuthSpecification, 1, orderId);
        DeleteOrder deleteOrder = new DeleteOrder(response);
        deleteOrder.hasStatusCodeOkCheck();
        orderId=null;
    }


    @Disabled
    @DisplayName("Негативный тест получения статуса заявки с некорректными данными")
    @Description("Проверка Get статуса заявки c с некорректными данными и проверкой status code")
    @ParameterizedTest(name = "{index} => статус заявки {0}")
    @CsvSource(delimiter = ',', textBlock = """
         data with empty orderId, '                                       '
    """)
    public void testGetStatusOrderNegative(String desc, String id) throws IOException {
        StatusOrder status = new StatusOrder(requestAuthSpecification, id);
        status.hasStatusCodeBadCheck();
    }

    @DisplayName("Негативный тест получения исключения статуса заявки с некорректными данными")
    @ParameterizedTest(name = "{index} => Проверка на исключение статуса заявки {0}")
    @CsvSource(delimiter = ',', textBlock = """
         data with empty orderId, '                                       '
    """)
    public void fixMeFail(String desc, String id) {
        assertThrows(AssertionError.class, () -> testGetStatusOrderNegative(desc, id));
    }

    @DisplayName("Негативный тест удаления заявки на кредит")
    @Description("Проверка Delete заявки с некорректными данными")
    @ParameterizedTest(name = "{index} => запрос Delete заявки {0}")
    @CsvSource(delimiter = ',', textBlock = """
         data with minus userId, -1, 262523ca-69f8-443e-bff9-721f25197bb8
         data with zero orderId, 1, 00000000-0000-0000-0000-000000000000
         data with zero userId and orderId, 0, 00000000-0000-0000-0000-000000000000
    """)
    public void testDeleteOrderStatusCode(String desc, long userId, String orderId) throws IOException {
         Response response = DeleteOrder.checkDeleteOrder(requestAuthSpecification, userId, orderId);
         DeleteOrder deleteOrder = new DeleteOrder(response);
         deleteOrder.hasStatusCodeBadCheck();
    }

    @Order(3)
    @DisplayName("Позитивный тест заявки на кредит")
    @Description("Проверка POST заявки (userId: 1, tariffId: 1)")
    @Test
    public void testPostOrderPositive() throws IOException {
        ValidatableResponse response = PostOrder.checkOrder(requestAuthSpecification, 1, 1);
        PostOrder order = new PostOrder(response);
        order.hasStatusCodeOkCheck();
        order.hasNotEmptyCheck();
        orderId = order.saveOrderId(1).getOrderId();
    }

    @Order(4)
    @Feature("Статус заявки")
    @DisplayName("Позитивный тест статуса заявки IN_PROGRESS на кредит (userId: 1, tariffId: 1)")
    @Description("Проверка GET статуса IN_PROGRESS")
    @DisabledIf("hasОrderNull")
    @ResourceLock(value = "orderId")
    @Test
    public void testGetStatusOrderPositive() throws IOException {
        StatusOrder status = new StatusOrder(requestAuthSpecification, orderId);
        status.hasStatusOrderCheck("IN_PROGRESS");
    }

    @Order(5)
    @Feature("Статус заявки")
    @DisplayName("Позитивный тест статуса заявки APPROVED или REFUSED (userId: 1, tariffId: 1)")
    @Description("Проверка GET на статус APPROVED или REFUSED")
    @DisabledIf("hasОrderNull")
    @ResourceLock(value = "orderId")
    @Test
    public void testWaitGetStatusOrderPositive() throws IOException, InterruptedException {
        Thread.sleep(99900);
        await().pollInterval(2000, TimeUnit.MILLISECONDS).until(() -> {
            StatusOrder statused = new StatusOrder(requestAuthSpecification, orderId);
            return  statused.hasStatusRefusedCheck() || statused.hasStatusApprovedCheck();
        });
    }

    @Order(6)
    @Feature("Статус заявки")
    @DisplayName("Негативный тест удаления заявки. Статус ORDER_IMPOSSIBLE_TO_DELETE")
    @Description("Проверка ORDER_IMPOSSIBLE_TO_DELETE запроса Delete рассмотренной заявки")
    @DisabledIf("hasОrderNull")
    @ResourceLock(value = "orderId")
    @ParameterizedTest(name = "{index} => проверка на удаление рассмотренной заявки userId = {0}")
    @CsvSource({"1"})
    public void testDeleteOrderNegative(long userId) throws IOException {
        Response responseDel = DeleteOrder.checkDeleteOrder(requestAuthSpecification, userId, orderId);
        DeleteOrder deleteOrder = new DeleteOrder(responseDel);
        deleteOrder.hasStatusCodeBadCheck();
    }

    boolean hasОrderNull() {
        return (orderId == null);
    }
}
