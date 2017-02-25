package com.clovercoin.pillowing;

import com.clovercoin.pillowing.constant.Action;
import com.clovercoin.pillowing.constant.ErrorCode;
import com.clovercoin.pillowing.constant.Status;
import com.clovercoin.pillowing.service.ConstantServiceImpl;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class ConstantServiceImplTests {
    private ConstantServiceImpl constantService;

    @Before
    public void Setup() {
        constantService = new ConstantServiceImpl();
    }

    private static final String nonexistantConstantName = "Please don't name a constant this. It's really not a good plan";

    @Test(expected=IllegalArgumentException.class)
    public void Get_Should_ThrowIllegalArgumentException_When_ConstantNotDefined() {
        constantService.get(nonexistantConstantName);
    }

    @Test(expected=IllegalArgumentException.class)
    public void GetString_Should_ThrowIllegalArgumentException_When_ConstantNotDefined() {
        constantService.getString(nonexistantConstantName);
    }

    @Test(expected=IllegalArgumentException.class)
    public void GetAllWithPrefix_Should_ThrowIllegalArgumentException_When_NoneExistWithGivenPrefix() {
        constantService.getAllWithPrefix(nonexistantConstantName);
    }

    //Action tests
    @Test
    public void Get_ShouldBeNotNull_When_GivenAnyActionWithPrefixACTION_() {
        for (Action a : Action.values()) {
            Object result = constantService.get("ACTION_" + a.name());
            assertThat(result).isNotNull();
        }
    }

    @Test
    public void GetString_ShouldBeNotNull_When_GivenAnyActionWithPrefixACTION_() {
        for (Action a : Action.values()) {
            Object result = constantService.get("ACTION_" + a.name());
            assertThat(result).isNotIn(null, "null");
        }
    }
    
    @Test
    public void getAllWithPrefix_ShouldReturnAllActions_When_GivenPrefixACTION_() {
        Map<String, Object> results = constantService.getAllWithPrefix("ACTION_");
        for (Action a: Action.values()) {
            assertThat(results.containsKey("ACTION_" + a.name())).isEqualTo(true);
        }
    }

    //Status tests
    @Test
    public void Get_ShouldBeNotNull_When_GivenAnyStatusWithPrefixSTATUS_() {
        for (Status s : Status.values()) {
            Object result = constantService.get("STATUS_" + s.name());
            assertThat(result).isNotNull();
        }
    }

    @Test
    public void GetString_ShouldBeNotNull_When_GivenAnyStatusWithPrefixSTATUS_() {
        for (Status s : Status.values()) {
            Object result = constantService.get("STATUS_" + s.name());
            assertThat(result).isNotIn(null, "null");
        }
    }

    @Test
    public void getAllWithPrefix_ShouldReturnAllStatuss_When_GivenPrefixSTATUS_() {
        Map<String, Object> results = constantService.getAllWithPrefix("STATUS_");
        for (Status a: Status.values()) {
            assertThat(results.containsKey("STATUS_" + a.name())).isEqualTo(true);
        }
    }

    //ErrorCode tests
    @Test
    public void Get_ShouldBeNotNull_When_GivenAnyErrorWithPrefixERROR_() {
        for (ErrorCode a : ErrorCode.values()) {
            Object result = constantService.get("ERROR_" + a.name());
            assertThat(result).isNotNull();
        }
    }

    @Test
    public void GetString_ShouldBeNotNull_When_GivenAnyErrorWithPrefixERROR_() {
        for (ErrorCode a : ErrorCode.values()) {
            Object result = constantService.get("ERROR_" + a.name());
            assertThat(result).isNotIn(null, "null");
        }
    }

    @Test
    public void getAllWithPrefix_ShouldReturnAllErrorCodes_When_GivenPrefixError_() {
        Map<String, Object> results = constantService.getAllWithPrefix("ERROR_");
        for (ErrorCode a: ErrorCode.values()) {
            assertThat(results.containsKey("ERROR_" + a.name())).isEqualTo(true);
        }
    }
}
