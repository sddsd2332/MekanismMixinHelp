package mekmixinhelp.common;


import io.netty.buffer.ByteBuf;
import mekanism.common.Mekanism;
import mekanism.common.Version;
import mekanism.common.base.IModule;
import mekanism.common.config.MekanismConfig;
import mekanism.common.network.PacketSimpleGui;
import mekmixinhelp.common.config.MekceuMixinConfig;
import mekmixinhelp.mekmixinhelp.Tags;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.util.CompoundDataFixer;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import java.io.File;

@Mod(modid = MekanismMixinHelp.MODID, useMetadata = true)
@Mod.EventBusSubscriber()
public class MekanismMixinHelp implements IModule {

    public static final String MODID = Tags.MOD_ID;

    @SidedProxy(clientSide = "mekmixinhelp.client.MekanismMixinHelpClientProxy", serverSide = "mekmixinhelp.common.MekanismMixinHelpCommonProxy")
    public static MekanismMixinHelpCommonProxy proxy;

    @Mod.Instance(MekanismMixinHelp.MODID)
    public static MekanismMixinHelp instance;

    public static Version versionNumber = new Version(999, 999, 999);
    public static final int DATA_VERSION = 1;

    public static Configuration config;

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        Mekanism.modulesLoaded.add(this);
        PacketSimpleGui.handlers.add(proxy);
        NetworkRegistry.INSTANCE.registerGuiHandler(this, new MekanismMixinHelpGuiHandler());
        MinecraftForge.EVENT_BUS.register(this);
        CompoundDataFixer fixer = FMLCommonHandler.instance().getDataFixer();
        Mekanism.logger.info("Loaded Mekanism Mixin Help module.");
    }

    @Override
    public Version getVersion() {
        return versionNumber;
    }

    @Override
    public String getName() {
        return "MixinHELP";
    }


    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        config = new Configuration(new File("config/mekanism/MekanismMixinHelp.cfg"));
        loadConfiguration();
    }

    @Override
    public void writeConfig(ByteBuf byteBuf, MekanismConfig mekanismConfig) {

    }

    @Override
    public void readConfig(ByteBuf byteBuf, MekanismConfig mekanismConfig) {

    }

    @Override
    public void resetClient() {

    }

    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(Mekanism.MODID) || event.getModID().equals(MekanismMixinHelp.MODID)) {
            loadConfiguration();
        }
    }

    public void loadConfiguration() {
        MekceuMixinConfig.local().config.load(config);
        if (config.hasChanged()) {
            config.save();
        }
    }

}
