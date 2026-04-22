package me.aleksilassila.litematica.printer;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fi.dy.masa.malilib.event.InitializationHandler;
import me.aleksilassila.litematica.printer.printer.zxy.inventory.OpenInventoryPacket;
import me.aleksilassila.litematica.printer.config.Configs;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
//#if MC >= 11902
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
//#endif
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public class LitematicaPrinterMod implements ModInitializer, ClientModInitializer {
    // 👉 服务端+客户端通用逻辑（仅放无客户端依赖的代码）
    // 例如：注册网络包、通用配置加载（无GUI）、数据生成等
    @Override
    public void onInitialize() {
        OpenInventoryPacket.init();
        OpenInventoryPacket.registerReceivePacket();
    }

    //#if MC >= 11902
    public static LiteralArgumentBuilder<FabricClientCommandSource> buildCommand(){
        LiteralArgumentBuilder<FabricClientCommandSource> builder = LiteralArgumentBuilder.literal("ofts-printer");
        builder.executes(a -> 1);
        builder.then(LiteralArgumentBuilder.<FabricClientCommandSource>literal("enable")
                .executes(LitematicaPrinterMod::onEnable));
        builder.then(LiteralArgumentBuilder.<FabricClientCommandSource>literal("disable")
                .executes(LitematicaPrinterMod::onDisable));
        return builder;
    }

    private static int onEnable(CommandContext<FabricClientCommandSource> ctx){
        assert Minecraft.getInstance().player != null;
        //#if MC >= 260100
        Minecraft.getInstance().player.sendSystemMessage(Component.literal("Printer Enabled!").withColor(0x00FFFF));
        //#else
        Minecraft.getInstance().player.displayClientMessage(Component.literal("Printer Enabled!").withColor(0x00FFFF), false);
        //#endif
        Configs.Core.WORK_SWITCH.setBooleanValue(true);
        return 1;
    }

    private static int onDisable(CommandContext<FabricClientCommandSource> ctx){
        assert Minecraft.getInstance().player != null;
        //#if MC >= 260100
        Minecraft.getInstance().player.sendSystemMessage(Component.literal("Printer Disabled!").withColor(0x00FFFF));
        //#else
        Minecraft.getInstance().player.displayClientMessage(Component.literal("Printer Disabled!").withColor(0x00FFFF), false);
        //#endif
        Configs.Core.WORK_SWITCH.setBooleanValue(false);
        return 1;
    }
    //#endif

    // 👉 仅客户端逻辑（放心使用客户端API）
    // 比如：注册按键、GUI、渲染钩子、客户端配置界面等
    @Override
    public void onInitializeClient() {
        OpenInventoryPacket.registerClientReceivePacket();
        InitializationHandler.getInstance().registerInitializationHandler(new InitHandler());
        //#if MC >= 11902
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, b) -> dispatcher.register(LitematicaPrinterMod.buildCommand()));
        //#endif
    }
}
