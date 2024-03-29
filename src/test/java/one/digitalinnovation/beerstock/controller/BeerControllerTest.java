package one.digitalinnovation.beerstock.controller;

import one.digitalinnovation.beerstock.builder.BeerDTOBuilder;
import one.digitalinnovation.beerstock.dto.BeerDTO;
import one.digitalinnovation.beerstock.exception.BeerNotFoundException;
import one.digitalinnovation.beerstock.service.BeerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import java.util.Collections;

import static one.digitalinnovation.beerstock.utils.JsonConvertionUtils.asJsonString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class BeerControllerTest {

    private static final String BEER_API_URL_PATH = "/api/v1/beers";
    private static final long VALID_BEER_ID = 1L;
    private static final long INVALID_BEER_ID = 2L;
    private static final String BEER_API_SUBPATH_INCREMET_URL = "/incremet";
    private static final String BEER_API_SUBPATH_DECREMET_URL = "/decremet";

    private MockMvc mockMvc;

    @Mock
    private BeerService beerService;

    @InjectMocks
    private BeerController beerController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(beerController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setViewResolvers((s, locale) -> new MappingJackson2JsonView())
                .build();
    }


    @Test
    void whenPOSTItsCalledThenABeerIsCreated() throws Exception {
        //given
        BeerDTO beerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
        //when
        when(beerService.createBeer(beerDTO)).thenReturn(beerDTO);
        //then
        mockMvc.perform(post(BEER_API_URL_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(beerDTO)))
                .andExpect(status().isCreated());
                /*.andExpect((ResultMatcher) jsonPath("$.name", is(beerDTO.getName())))
                .andExpect((ResultMatcher) jsonPath("$.brand", is(beerDTO.getBrand())))
                .andExpect((ResultMatcher) jsonPath("$.type", is(beerDTO.getType())));*/


    }
    @Test
    void whenPOSTItsCalledWithoutRequiredFieldThenAnErrorIsReturned() throws Exception {
        //given
        BeerDTO beerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
        beerDTO.setBrand(null);
        //then
        mockMvc.perform(post(BEER_API_URL_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(beerDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenGETIsCalledWithValidNameThenOkStatusIsReturned() throws Exception {
        BeerDTO beerDTO = BeerDTOBuilder.builder().build().toBeerDTO();

        when(beerService.findByName(beerDTO.getName())).thenReturn(beerDTO);

        mockMvc.perform(MockMvcRequestBuilders.get(BEER_API_URL_PATH + "/" + beerDTO.getName())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
                /*.andExpect(jsonPath("$.name", is(beerDTO.getName())))
                .andExpect(jsonPath("$.brand", is(beerDTO.getBrand())))
                .andExpect(jsonPath("$.type", is(beerDTO.getType().toString())));*/
    }

    @Test
    void whenGETIsCalledWithoutRegisteredNameThenNotFoundStatusIsReturned() throws Exception {
        BeerDTO beerDTO = BeerDTOBuilder.builder().build().toBeerDTO();

        when(beerService.findByName(beerDTO.getName())).thenThrow(BeerNotFoundException.class);

        mockMvc.perform(MockMvcRequestBuilders.get(BEER_API_URL_PATH + "/" + beerDTO.getName())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenGETListWithBeersIsCalledThenOkStatusIsReturned() throws Exception {
        BeerDTO beerDTO = BeerDTOBuilder.builder().build().toBeerDTO();

        when(beerService.listAll()).thenReturn(Collections.singletonList(beerDTO));

        mockMvc.perform(MockMvcRequestBuilders.get(BEER_API_URL_PATH)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
                /*.andExpect(jsonPath("$[0].name", is(beerDTO.getName())))
                .andExpect(jsonPath("$[0].brand", is(beerDTO.getBrand())))
                .andExpect(jsonPath("$[0].type", is(beerDTO.getType().toString())));*/
    }

    @Test
    void whenGETListWithoutBeersIsCalledThenOkStatusIsReturned() throws Exception {
        BeerDTO beerDTO = BeerDTOBuilder.builder().build().toBeerDTO();

        when(beerService.listAll()).thenReturn(Collections.singletonList(beerDTO));

        mockMvc.perform(MockMvcRequestBuilders.get(BEER_API_URL_PATH)
                 .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void whenDELETEIsCalledWithValidIdThenNoContentStatusIsReturned() throws Exception {
        BeerDTO beerDTO = BeerDTOBuilder.builder().build().toBeerDTO();

        doNothing().when(beerService).deleteById(beerDTO.getId());

        mockMvc.perform(MockMvcRequestBuilders.delete(BEER_API_URL_PATH + "/" + beerDTO.getId())
                 .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void whenDELETEIsCalledWithInvalidIdThenNotFoundStatusIsReturned() throws Exception {
        doThrow(BeerNotFoundException.class).when(beerService).deleteById(INVALID_BEER_ID);

        mockMvc.perform(MockMvcRequestBuilders.delete(BEER_API_URL_PATH + "/" + INVALID_BEER_ID)
                 .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}

