package mod.adrenix.nostalgic.client.gui.screen.home.overlay.supporter;

import mod.adrenix.nostalgic.util.client.renderer.InternetTexture;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;

record PlayerFace(Identifier location, InternetTexture texture) {
    public void register() {
        Minecraft.getInstance().getTextureManager().register(this.location, this.texture);
    }
}
