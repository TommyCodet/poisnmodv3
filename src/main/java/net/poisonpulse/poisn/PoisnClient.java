package net.poisonpulse.poisn;

import net.fabricmc.api.ClientModInitializer;
import net.poisonpulse.poisn.nametag.Members;
import net.poisonpulse.poisn.price.PriceManager;
import net.poisonpulse.poisn.price.PriceTooltip;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PoisnClient implements ClientModInitializer {
    public static final String MOD_ID = "poisn";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitializeClient() {
        LOGGER.info("PoisonPulse (POISN) starting – offline mode");
        Members.init();
        PriceManager.load();
        PriceTooltip.register();
        LOGGER.info("PoisonPulse ready: nametags + item prices");
    }
}
