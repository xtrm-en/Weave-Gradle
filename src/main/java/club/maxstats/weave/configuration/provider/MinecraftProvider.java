package club.maxstats.weave.configuration.provider;

import club.maxstats.weave.util.Constants;
import club.maxstats.weave.util.DownloadUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class MinecraftProvider {
    /*TODO Replace 1.8.9 with version specified by extension */
    private String version = "1.8.9";
    private JsonObject versionJson;
    private String downloadPath;

    public void provide() {
        JsonObject manifestJson = DownloadUtil.getJsonFromURL("https://launchermeta.mojang.com/mc/game/version_manifest_v2.json");
        if (manifestJson != null) {
            JsonArray versionArray = manifestJson.getAsJsonArray("versions");
            JsonObject versionObject = null;

            for (int i = 0; i < versionArray.size(); i++) {
                JsonObject version = versionArray.get(i).getAsJsonObject();
                if (version.get("id").getAsString().equals(this.version)) {
                    versionObject = version;
                    break;
                }
            }

            if (versionObject != null) {
                JsonObject versionJson = DownloadUtil.getJsonFromURL(versionObject.get("url").getAsString());
                if (versionJson != null) {
                    this.versionJson = versionJson;
                    this.downloadPath = Constants.CACHE_DIR + "/" + this.version;

                    JsonObject downloadsObject = versionJson.getAsJsonObject("downloads");
                    JsonObject clientObject = downloadsObject.getAsJsonObject("client");

                    String clientURL = clientObject.get("url").getAsString();
                    String checksum = clientObject.get("sha1").getAsString();

                    DownloadUtil.downloadAndChecksum(clientURL, checksum, this.downloadPath);

                    new MinecraftLibraryProvider(this).provide();
                }
            }
        }
    }

    public String getVersion() { return this.version; }
    public String getDownloadPath() { return this.downloadPath; }
    public JsonObject getVersionJson() { return this.versionJson; }
}