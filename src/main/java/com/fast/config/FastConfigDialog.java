package com.fast.config;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.ui.DialogWrapper;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
import org.apache.commons.io.FileUtils;

public class FastConfigDialog extends DialogWrapper {
    private JRadioButton iosRadioButton;
    private JRadioButton androidRadioButton;
    private JRadioButton yesRecordRadioButton;
    private JRadioButton noRecordRadioButton;
    private JRadioButton yesResetSessionRadioButton;
    private JRadioButton noResetSessionRadioButton;
    private JComboBox<String> environmentComboBox;  // 新增下拉框

    public FastConfigDialog() {
        super(true); // 模态对话框
        setTitle("修改环境配置"); // 设置对话框标题
        init(); // 初始化
    }

    @Override
    protected JComponent createCenterPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); // 使用垂直方向的布局

        // 创建一个包含所有选项的面板（使用 GridLayout 来确保标签和选项横向对齐）
        JPanel optionsPanel = new JPanel(new GridLayout(4, 2, 10, 1));

        // 创建平台单选框
        iosRadioButton = new JRadioButton("ios");
        androidRadioButton = new JRadioButton("android");
        ButtonGroup platformGroup = new ButtonGroup();
        platformGroup.add(iosRadioButton);
        platformGroup.add(androidRadioButton);

        // 创建录像单选框
        yesRecordRadioButton = new JRadioButton("是");
        noRecordRadioButton = new JRadioButton("否");
        ButtonGroup recordGroup = new ButtonGroup();
        recordGroup.add(yesRecordRadioButton);
        recordGroup.add(noRecordRadioButton);

        // 创建重置会话单选框
        yesResetSessionRadioButton = new JRadioButton("是");
        noResetSessionRadioButton = new JRadioButton("否");
        ButtonGroup resetSessionGroup = new ButtonGroup();
        resetSessionGroup.add(yesResetSessionRadioButton);
        resetSessionGroup.add(noResetSessionRadioButton);

        // 创建下拉框
        String[] environments = {"ticket", "room", "customer", "wait-list", "fast-cash", "half & half", "half and half for substitute part 1", "ticket split", "time-punch", "order","hold and rush","future order"};

        Project project = ProjectManager.getInstance().getOpenProjects()[0];
        String projectRootPath = project.getBasePath();
        String configFilePath = projectRootPath + "/debug/config.ts";
        File configFile = new File(configFilePath);
        environmentComboBox = new JComboBox<>(new TSArrayConverter().getJavaArray(configFile));  // 创建下拉框并设置选项

        // 加载配置并设置选中状态
        loadConfig();

        // 添加标签和单选框
        optionsPanel.add(new JLabel("请选择运行平台:"));
        JPanel platformPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        platformPanel.add(iosRadioButton);
        platformPanel.add(androidRadioButton);
        optionsPanel.add(platformPanel);

        optionsPanel.add(new JLabel("是否录像:"));
        JPanel recordPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        recordPanel.add(yesRecordRadioButton);
        recordPanel.add(noRecordRadioButton);
        optionsPanel.add(recordPanel);

        optionsPanel.add(new JLabel("是否重置会话:"));
        JPanel resetSessionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        resetSessionPanel.add(yesResetSessionRadioButton);
        resetSessionPanel.add(noResetSessionRadioButton);
        optionsPanel.add(resetSessionPanel);

        // 创建一个面板，用于显示“请选择运行文件”下拉框
        optionsPanel.add(new JLabel("请选择运行文件:"));
        JPanel environmentPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        environmentPanel.add(environmentComboBox);
        optionsPanel.add(environmentPanel);

        // 将所有的选项面板添加到主面板
        panel.add(optionsPanel);

        return panel;
    }

    private void loadConfig() {
        Project project = ProjectManager.getInstance().getOpenProjects()[0];
        String projectRootPath = project.getBasePath();
        String configFilePath = projectRootPath + "/debug/config.ts";
        File configFile = new File(configFilePath);

        if (configFile.exists()) {
            try {
                // 每次重新打开文件，确保读取最新内容
                List<String> lines = FileUtils.readLines(configFile, "UTF-8");
                String platform = null;
                boolean isRecord = true;
                boolean resetSession = false;  // 默认不重置会话

                // 逐行处理文件内容
                for (String line : lines) {
                    if (line.contains("platform")) {
                        // 提取 platform 配置项
                        platform = line.split("=")[1].trim().replace("'", "").trim();
                        // 根据配置设置平台单选框
                        if ("android".equals(platform)) {
                            androidRadioButton.setSelected(true);
                        } else if ("ios".equals(platform)) {
                            iosRadioButton.setSelected(true);
                        }
                        System.out.println("platform: " + platform); // 处理 platform = ' android'
                    }
                    if (line.contains("isRecord")) {
                        // 提取 isRecord 配置项
                        isRecord = Boolean.parseBoolean(line.split("=")[1].trim());
                        System.out.println("isRecord: " + isRecord);
                        // 根据配置设置是否录像单选框
                        if (isRecord) {
                            yesRecordRadioButton.setSelected(true);
                        } else {
                            noRecordRadioButton.setSelected(true);
                        }
                    }
                    if (line.contains("isReset")) {
                        // 提取 resetSession 配置项
                        resetSession = Boolean.parseBoolean(line.split("=")[1].trim());
                        // 根据配置设置是否重置会话单选框
                        if (resetSession) {
                            yesResetSessionRadioButton.setSelected(true);
                        } else {
                            noResetSessionRadioButton.setSelected(true);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();  // 处理文件读取错误
            }
        }
    }

    // 获取用户选择的平台
    public String getSelectedPlatform() {
        if (iosRadioButton.isSelected()) {
            return "ios";
        } else if (androidRadioButton.isSelected()) {
            return "android";
        }
        return null; // 预防没有选择平台的情况
    }

    // 获取用户选择的是否录像配置
    public boolean isRecording() {
        return yesRecordRadioButton.isSelected();
    }

    // 获取用户选择的是否重置会话配置
    public boolean isResetSession() {
        return yesResetSessionRadioButton.isSelected();
    }

    public String getSelectedEnvironment() {
        return (String) environmentComboBox.getSelectedItem();
    }
}
