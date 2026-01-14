package mekmixinhelp.common.config;


public class MekceuMixinConfig {

    private static MekceuMixinConfig LOCAL = new MekceuMixinConfig();
    private static MekceuMixinConfig SERVER = null;

    public static MekceuMixinConfig current() {
        return SERVER != null ? SERVER : LOCAL;
    }

    public static MekceuMixinConfig local() {
        return LOCAL;
    }

    public MekceuMixinSetConfig config = new MekceuMixinSetConfig();
}
