package com.fast.config;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;

import javax.swing.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FastConfig extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        // 创建并显示自定义对话框
        FastConfigDialog dialog = new FastConfigDialog();
        dialog.show();

        if (dialog.isOK()) {
            // 获取对话框中的选择
            String selectedPlatform = dialog.getSelectedPlatform();  // 获取平台
            String isRecord = Boolean.toString(dialog.isRecording());  // 是否录像
            String isReset = Boolean.toString(dialog.isResetSession());  // 是否重置会话
            String selectedEnvironment = dialog.getSelectedEnvironment();  // 获取环境

            // 更新 config.ts 文件
            updateConfigFile(selectedPlatform, isRecord, isReset, selectedEnvironment);
        }
    }

    private void updateConfigFile(String platform, String isRecord, String isReset, String environment) {
        try {
            Project project = ProjectManager.getInstance().getOpenProjects()[0];
            String projectRootPath = project.getBasePath();
            System.out.println("项目根目录: " + projectRootPath);
            String configFilePath = projectRootPath + "/debug/config.ts";
            File configFile = new File(configFilePath);

            // 读取 config.ts 文件内容
            String content = new String(Files.readAllBytes(Paths.get(configFilePath)));

            // 替换平台配置
            content = content.replaceAll("export const platform = '\\w+'", "export const platform = '" + platform + "'");

            // 替换是否录像配置
            content = content.replaceAll("export const isRecord = \\w+", "export const isRecord = " + isRecord);

            // 替换是否重置会话配置
            content = content.replaceAll("export const isReset = \\w+", "export const isReset = " + isReset);

            // 替换环境配置
            content = content.replaceAll("export const environment = '.*?'", "export const environment = '" + environment + "'");

            // 将更新后的内容写回 config.ts 文件
            Files.write(Paths.get(configFilePath), content.getBytes());
            System.out.println("config.ts 文件更新成功！");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
