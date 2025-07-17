package mod.adrenix.nostalgic.util.client.search;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.LanguageInfo;
import net.minecraft.util.Mth;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

abstract class Database<T> implements LevenshteinDatabase<T>
{
    protected LanguageInfo languageInfo = this.getLanguage();
    protected Map<String, List<T>> map = new LinkedHashMap<>();
    protected double threshold = 0.01D;

    protected LanguageInfo getLanguage()
    {
        String code = Minecraft.getInstance().getLanguageManager().getSelected();

        return Minecraft.getInstance().getLanguageManager().getLanguage(code);
    }

    @Override
    public void setThreshold(double threshold)
    {
        this.threshold = Mth.clamp(threshold, 0.0D, 1.0D);
    }

    @Override
    public double getThreshold()
    {
        return this.threshold;
    }

    @Override
    public LevenshteinResult<T> levenshtein()
    {
        LanguageInfo language = this.getLanguage();

        if (this.languageInfo != language)
        {
            this.languageInfo = language;
            this.reset();
        }

        return LevenshteinResult.with(this.getDatabase(), this.threshold);
    }
}
