package ru.job4j.dreamjob.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.ui.ConcurrentModel;
import org.springframework.web.multipart.MultipartFile;
import ru.job4j.dreamjob.dto.FileDto;
import ru.job4j.dreamjob.model.Candidate;
import ru.job4j.dreamjob.model.City;
import ru.job4j.dreamjob.service.CandidateService;
import ru.job4j.dreamjob.service.CityService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

import static java.time.LocalDateTime.now;
import static org.mockito.Mockito.*;

class CandidateControllerTest {

    private CandidateService candidateService;

    private CityService cityService;

    private CandidateController candidateController;

    private MultipartFile testFile;

    @BeforeEach
    public void initServices() {
        candidateService = mock(CandidateService.class);
        cityService = mock(CityService.class);
        candidateController = new CandidateController(candidateService, cityService);
        testFile = new MockMultipartFile("testFile.img", new byte[] {1, 2, 3});
    }

    @Test
    public void whenRequestCandidatesListThenGetPageWithCandidates() {
        var candidate1 = new Candidate(1, "Senior1", "descr", now(), 1, 1);
        var candidate2 = new Candidate(1, "Senior2", "descr", now(), 2, 1);
        var expectedCandidateList = List.of(candidate1, candidate2);
        when(candidateService.findAll()).thenReturn(expectedCandidateList);

        var model = new ConcurrentModel();
        var view = candidateController.getAll(model);
        var actualCandidateList = model.getAttribute("candidates");

        assertThat(view).isEqualTo("candidates/list");
        assertThat(actualCandidateList).isEqualTo(expectedCandidateList);
    }

    @Test
    public void whenRequestCandidateCreationPageThenGetPageWithCities() {
        var city1 = new City(1, "Kukareki");
        var city2 = new City(2, "Vonuh");
        var expectedCities = List.of(city1, city2);
        when(cityService.findAll()).thenReturn(expectedCities);

        var model = new ConcurrentModel();
        var view = candidateController.getCreationPage(model);
        var actualCities = model.getAttribute("cities");

        assertThat(view).isEqualTo("candidates/create");
        assertThat(actualCities).isEqualTo(expectedCities);
    }

    @Test
    public void whenPostCandidateWithFileThenGetSameDataAndRedirectToPageWithCandidates() throws IOException {
        var candidate = new Candidate(1, "Senior", "descr", now(), 1, 1);
        var fileDto = new FileDto(testFile.getOriginalFilename(), testFile.getBytes());
        var candidateArgumentCaptor = ArgumentCaptor.forClass(Candidate.class);
        var fileDtoArgumentCaptor = ArgumentCaptor.forClass(FileDto.class);
        when(candidateService.save(candidateArgumentCaptor.capture(), fileDtoArgumentCaptor.capture())).thenReturn(candidate);

        var model = new ConcurrentModel();
        var view = candidateController.create(candidate, testFile, model);
        var actualCandidate = candidateArgumentCaptor.getValue();
        var actualFileDto = fileDtoArgumentCaptor.getValue();

        assertThat(view).isEqualTo("redirect:/candidates");
        assertThat(actualCandidate).usingRecursiveComparison().isEqualTo(candidate);
        assertThat(actualFileDto).usingRecursiveComparison().isEqualTo(fileDto);
    }

    @Test
    public void whenSomeExceptionThrownDuringCreateCandidateThenGetErrorPageWithMessage() {
        var expectedException = new RuntimeException("Failed to load file!");
        when(candidateService.save(any(), any())).thenThrow(expectedException);

        var model = new ConcurrentModel();
        var view = candidateController.create(new Candidate(), testFile, model);
        var actualErrorMessage = model.getAttribute("message");

        assertThat(view).isEqualTo("errors/404");
        assertThat(actualErrorMessage).isEqualTo(expectedException.getMessage());
    }

    @Test
    public void whenRequestCandidateByIdSuccessfullyThenGetConcreteCandidatePageWithCity() {
        var candidate = new Candidate(1, "Senior", "descr", now(), 1, 1);
        var city = new City(1, "Kolbasa");
        var expectedCities = List.of(city);
        when(candidateService.findById(anyInt())).thenReturn(Optional.of(candidate));
        when(cityService.findAll()).thenReturn(expectedCities);

        var model = new ConcurrentModel();
        var view = candidateController.getByID(model, anyInt());
        var actualCandidate = model.getAttribute("candidate");
        var actualCity = model.getAttribute("cities");

        assertThat(view).isEqualTo("candidates/edit");
        assertThat(actualCandidate).usingRecursiveComparison().isEqualTo(candidate);
        assertThat(actualCity).isEqualTo(expectedCities);
    }

    @Test
    public void whenRequestCandidateByIdWithWrongIdThenGetErrorPage() {
        var expectedErrorMessage = "Кандидат с указанным идентификатором не найден";
        when(candidateService.findById(anyInt())).thenReturn(Optional.empty());

        var model = new ConcurrentModel();
        var view = candidateController.getByID(model, anyInt());
        var actualErrorMessage = model.getAttribute("message");

        assertThat(view).isEqualTo("errors/404");
        assertThat(actualErrorMessage).isEqualTo(expectedErrorMessage);
    }

    @Test
    public void whenRequestCandidateUpdateThenGetUpdatedCandidate() throws IOException {
        var candidate = new Candidate(1, "Senior", "descr", now(), 1, 1);
        var fileDto = new FileDto(testFile.getOriginalFilename(), testFile.getBytes());
        var candidateArgumentCaptor = ArgumentCaptor.forClass(Candidate.class);
        var fileDtoArgumentCaptor = ArgumentCaptor.forClass(FileDto.class);
        when(candidateService.update(candidateArgumentCaptor.capture(), fileDtoArgumentCaptor.capture())).thenReturn(true);

        var model = new ConcurrentModel();
        var view = candidateController.update(candidate, testFile, model);
        var actualCandidate = candidateArgumentCaptor.getValue();
        var actualFileDto = fileDtoArgumentCaptor.getValue();

        assertThat(view).isEqualTo("redirect:/candidates");
        assertThat(actualCandidate).usingRecursiveComparison().isEqualTo(candidate);
        assertThat(actualFileDto).usingRecursiveComparison().isEqualTo(fileDto);
    }

    @Test
    public void whenRequestCandidateUpdateWithWrongIdThenGetErrorPage() {
        var expectedErrorMessage = "Кандидат с указанным идентификатором не найден";
        when(candidateService.update(any(), any())).thenReturn(false);

        var model = new ConcurrentModel();
        var view = candidateController.update(new Candidate(), testFile, model);
        var actualErrorMessage = model.getAttribute("message");

        assertThat(view).isEqualTo("errors/404");
        assertThat(actualErrorMessage).isEqualTo(expectedErrorMessage);
    }

    @Test
    public void whenSomeExceptionThrownDuringUpdateCandidateThenGetErrorPage() {
        var expectedError = new RuntimeException("Failed to load candidate picture");
        when(candidateService.update(any(), any())).thenThrow(expectedError);

        var model = new ConcurrentModel();
        var view = candidateController.update(new Candidate(), testFile, model);
        var actualErrorMessage = model.getAttribute("message");

        assertThat(view).isEqualTo("errors/404");
        assertThat(actualErrorMessage).isEqualTo(expectedError.getMessage());
    }

    @Test
    public void whenRequestCandidateDeleteSuccessfullyThenRedirectToCandidates() {
        when(candidateService.deleteById(anyInt())).thenReturn(true);

        var model = new ConcurrentModel();
        var view = candidateController.delete(model, anyInt());
        var actualCandidates = model.getAttribute("candidates");

        assertThat(view).isEqualTo("redirect:/candidates");
        assertThat(actualCandidates).isNull();
    }

    @Test
    public void whenRequestCandidateDeleteWithWrongIdThenGetErrorPage() {
        var expectedErrorMessage = "Кандидат с указанным идентификатором не найден";
        when(candidateService.deleteById(anyInt())).thenReturn(false);

        var model = new ConcurrentModel();
        var view = candidateController.delete(model, anyInt());
        var actualErrorMessage = model.getAttribute("message");

        assertThat(view).isEqualTo("errors/404");
        assertThat(actualErrorMessage).isEqualTo(expectedErrorMessage);
    }
}