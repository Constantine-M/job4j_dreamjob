package ru.job4j.dreamjob.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import ru.job4j.dreamjob.dto.FileDto;
import ru.job4j.dreamjob.service.FileService;

import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

import static org.mockito.Mockito.*;

class FileControllerTest {

    private FileService fileService;

    private MultipartFile testFile;

    private FileController fileController;

    @BeforeEach
    public void initServices() {
        fileService = mock(FileService.class);
        fileController = mock(FileController.class);
        testFile = new MockMultipartFile("testFile.img", new byte[] {1, 2, 3});
    }

    @Test
    public void whenRequestFileByIdSuccessfully() throws IOException {
        var expectedContent = new FileDto(testFile.getOriginalFilename(), testFile.getBytes());
        when(fileService.findById(anyInt())).thenReturn(Optional.of(expectedContent));

        var view = fileController.getById(anyInt());

        assertThat(view).isEqualTo(ResponseEntity.ok(expectedContent.getContent()));
    }

}