package com.aska.core.controller;

import com.aska.core.dto.ErrorResponse;
import com.aska.core.dto.ProjectRequest;
import com.aska.core.dto.ProjectResponse;
import com.aska.core.dto.ProjectUpdateRequest;
import com.aska.core.service.IProjectService;
import com.aska.core.service.impl.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Tag(name = "Товары", description = "Добавление, просмотр, изменение и удаление товаров пользователя")
@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController{

    private final IProjectService projectService;

    @Operation(
            summary = "Список товаров",
            description = "Возвращает все товары текущего пользователя"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Список товаров получен")
    @GetMapping
    public List<ProjectResponse> list(Principal principal) {
        return projectService.listProject(principal.getName());
    }

    @Operation(
            summary = "Добавить товар",
            description = "Добавляет новый товар по ссылке на маркетплейс"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Товар добавлен"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Некорректные данные запроса",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectResponse create(@Valid @RequestBody ProjectRequest request, Principal principal) {
        return projectService.createProject(request, principal.getName());
    }

    @Operation(
            summary = "Получить товар",
            description = "Возвращает товар текущего пользователя по идентификатору"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Товар получен"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Товар не найден",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/{id}")
    public ProjectResponse get(@PathVariable Long id, Principal principal) {
        return projectService.getProject(id,principal.getName());
    }

    @Operation(
            summary = "Изменить товар",
            description = "Изменяет название и изображение товара"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Товар изменен"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Некорректные данные запроса",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Товар не найден",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PutMapping("/{id}")
    public ProjectResponse update(@PathVariable Long id, @Valid @RequestBody ProjectUpdateRequest request, Principal principal) {
        return projectService.updateProject(id,request, principal.getName());
    }

    @Operation(
            summary = "Удалить товар",
            description = "Удаляет товар пользователя вместе с его анализами"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Товар удален"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Товар не найден",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void  delete(@PathVariable Long id, Principal principal) {
        projectService.deleteProject(id,principal.getName());
    }
}