package mekmixinhelp.common.config;

import mekanism.common.config.BaseConfig;
import mekanism.common.config.options.BooleanOption;
import mekanism.common.config.options.StringListOption;

public class MekceuMixinSetConfig extends BaseConfig {

    //龙研
    public BooleanOption DEUpgrade = new BooleanOption(this, "DEUpgrade", false, "Can use the Tier Installer to upgrade the DE's Crafting Injector");

    //封包龙研
    public BooleanOption PackagedDEUpgrade = new BooleanOption(this, "PackagedDEUpgrade", false, "Can use the Tier Installer to upgrade the Packaged Draconic Crafting Injector");

    //额外植物学
    public BooleanOption closeAFORing = new BooleanOption(this, "Close AFO Ring", true, "Turn off the work of the Ring of Omnipotence");
    //盖亚三缴械modid白名单
    public final StringListOption GaiaIIIValidMods =new StringListOption(this,"GaiaIII Disarm Whitelist Mod id",new String[]{"botania", "extrabotany", "minecraft"},"Whitelist the Mod Id associated with the disarming of ExtraBotany.");
    //盖亚三饰品栏相关
    public BooleanOption GaiaIIIenabledBaubles = new BooleanOption(this, "GaiaIII Baubles checked", false, "Gaia Guardian III was allowed to inspect Baubles");
    //空律缴械物品白名单
    public final StringListOption VoidHerrscherValidItem = new StringListOption(this,"Void Herrscher Whitelist Item", new String[]{}, "Herrscher of the Void disarming items whitelist,syntax: modid:name or modid:name:meta");
    //空律缴械modid白名单
    public final StringListOption VoidHerrscherValidMods =new StringListOption(this,"Herrscher of the Void Whitelist Mod id", new String[]{"botania", "extrabotany", "minecraft"}, "Whitelist the Mod Id associated with the disarming of ExtraBotany.");
    //空律饰品栏相关
    public BooleanOption VoidHerrscherenabledBaubles = new BooleanOption(this, "Herrscher of the Void Baubles checked", false, "Herrscher of the Void was allowed to inspect Baubles");

    @Override
    public String getCategory() {
        return "mekceumixin";
    }
}
