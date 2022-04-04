package github.acodervic.mod.net.apikey;

import static github.acodervic.mod.data.BaseUtil.nullCheck;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import github.acodervic.mod.data.Opt;

public class APIKeyPool {
    LinkedList<APIKey> keys = new LinkedList<APIKey>();

    /**
     * 添加apikey
     *
     * @param key
     */
    public void addApiKey(APIKey key) {
        nullCheck(key);
        this.keys.add(key);
    }

    /**
     * 删除apikey
     *
     * @param key
     */
    public void removeApiKey(APIKey key) {
        nullCheck(key);
        this.keys.remove(key);
    }

    /**
     * 清空apikey
     *
     * @param key
     */
    public void clearApiKey() {
        this.keys.clear();
    }

    /**
     * 读取可用的apikey
     *
     * @return
     */
    public List<APIKey> getAvailableApiKeys() {
        return keys.stream().filter(k -> k.getAvailable()).collect(Collectors.toList());
    }

    /**
     * 读取可用的apikey
     *
     * @return
     */
    public Opt<APIKey> getAvailableApiKey() {
        return new Opt<>(getAvailableApiKeys().get(0));
    }
}
