package ru.practicum.shareit.item.model;

import lombok.Data;
import ru.practicum.shareit.exteption.NullAllowed;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class Item {
    private Integer id;
    @NotBlank(groups = NullAllowed.class)
    private String name;
    @NotBlank(groups = NullAllowed.class)
    private String description;
    @NotNull(groups = NullAllowed.class)
    private Boolean available;
    private Integer owner = 0;

}
