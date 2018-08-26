package ru.besttuts.stockwidget.provider.model.wrap;

import ru.besttuts.stockwidget.provider.model.Model;
import ru.besttuts.stockwidget.provider.model.Setting;

public class ModelSetting {
    private Setting setting;
    private Model model;

    public ModelSetting(Setting setting, Model model) {
        this.setting = setting;
        this.model = model;
    }

    public Setting getSetting() {
        return setting;
    }

    public void setSetting(Setting setting) {
        this.setting = setting;
    }

    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    @Override
    public String toString() {
        return "ModelSetting{" +
                "setting=" + setting +
                ", model=" + model +
                '}';
    }
}
