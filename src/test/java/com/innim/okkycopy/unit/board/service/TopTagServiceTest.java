package com.innim.okkycopy.unit.board.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.innim.okkycopy.domain.board.dto.response.TopTagListResponse;
import com.innim.okkycopy.domain.board.repository.TopTagRepository;
import com.innim.okkycopy.domain.board.service.TopTagService;
import java.util.Collections;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TopTagServiceTest {

    @Mock
    TopTagRepository topTagRepository;

    @InjectMocks
    TopTagService topTagService;

    @Nested
    class _findAll {

        @Test
        void given_invoke_then_responseTopTagListResponse() {
            // given
            given(topTagRepository.findAll()).willReturn(Collections.emptyList());

            // when
            Object result = topTagService.findAll();

            // then
            then(topTagRepository).should(times(1)).findAll();
            then(topTagRepository).shouldHaveNoMoreInteractions();
            assertThat(result).isInstanceOf(TopTagListResponse.class);
        }

    }

}
