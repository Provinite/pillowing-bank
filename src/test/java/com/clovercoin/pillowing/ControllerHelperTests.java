package com.clovercoin.pillowing;

import com.clovercoin.pillowing.constant.Action;
import com.clovercoin.pillowing.constant.ErrorCode;
import com.clovercoin.pillowing.constant.Status;
import com.clovercoin.pillowing.service.ConstantService;
import com.clovercoin.pillowing.service.ControllerHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.ui.Model;
import org.springframework.validation.support.BindingAwareModelMap;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ControllerHelperTests {
	@Autowired
	private ControllerHelper controllerHelper;

	@Autowired
	private ConstantService constantService;

	private Model model;

	@Before
	public void setup() {
		model = new BindingAwareModelMap();
	}

	@Test
	public void Should_SetActionInModel_When_GivenAnyAction() {
		for (Action a : Action.values()) {
			controllerHelper.setAction(model, a);
			Object action = model.asMap().get(constantService.getString("MODEL_KEY_ACTION"));

			assertThat(action).isEqualTo(constantService.get("ACTION_" + a.name()));
		}
	}

	@Test
	public void Should_SetStatusInModel_When_GivenAnyStatus() {
		for (Status s : Status.values()) {
			controllerHelper.setStatus(model, s);
			Object status = model.asMap().get(constantService.getString("MODEL_KEY_STATUS"));

			assertThat(status).isEqualTo(constantService.get("STATUS_" + s.name()));
		}
	}

	@Test
	public void SetError_Should_SetErrorInModel_When_GivenAnyError() {
		for (ErrorCode e : ErrorCode.values()) {
			controllerHelper.setError(model, e);
			Object error = model.asMap().get(constantService.getString("MODEL_KEY_ERROR"));

			assertThat(error).isEqualTo(constantService.get("ERROR_" + e.name()));
		}
	}

	@Test
	public void SetMessage_Should_SetMessageInModel_When_GivenAnyString() {
		String expectedMessage = "Some test message here!";
		controllerHelper.setMessage(model, expectedMessage);
		String actualMessage = String.valueOf(model.asMap().get(constantService.getString("MODEL_KEY_MESSAGE")));

		assertThat(actualMessage).isEqualTo(expectedMessage);
	}

	@Test(expected = IllegalArgumentException.class)
	public void Constructor_Should_ThrowIllegalArgumentException_When_GivenNoConstantService() {
		new ControllerHelper(null);
	}
}
