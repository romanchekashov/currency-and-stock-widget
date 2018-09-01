package ru.besttuts.stockwidget.provider.model.wrap;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.besttuts.stockwidget.provider.model.Model;
import ru.besttuts.stockwidget.provider.model.Setting;

@Data
@AllArgsConstructor
public class ModelSetting {
    private Setting setting;
    private Model model;
}
