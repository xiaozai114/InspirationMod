package com.starcrosser.inspiration.machine.plugin;

import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.component.IComponentHarness;
import com.hrznstudio.titanium.component.energy.EnergyStorageComponent;
import com.hrznstudio.titanium.container.addon.IContainerAddon;
import com.hrznstudio.titanium.container.addon.IntReferenceHolderAddon;
import com.hrznstudio.titanium.container.referenceholder.FunctionReferenceHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * 容量可被插件改写的能量缓冲。
 * <p>
 * Titanium 的 {@link EnergyStorageComponent} 容量在构造时固定，而电容插件要求运行期间
 * 动态扩容 / 缩容，因此这里直接读写父类 {@code EnergyStorage} 里 protected 的容量字段。
 * <p>
 * 另外补了一条容量的菜单同步：基类只把「当前存量」注册成 {@code DataSlot}，
 * 所以服务端改新容量后客户端那份 {@code EnergyStorageComponent} 仍停留在构造时的容量，
 * 界面上的能量条会按错误的上限换算（表现为 tooltip 里的上限纹丝不动、存量比例失真）。
 * 这里在基类提供的同步项之后再挂一个同步容量的 {@code DataSlot}，客户端收到后写回同一块缓冲区。
 */
public class PluginEnergyStorage<T extends IComponentHarness> extends EnergyStorageComponent<T> {

    public PluginEnergyStorage(int capacity, int maxReceive, int maxExtract, int x, int y) {
        super(capacity, maxReceive, maxExtract, x, y);
    }

    /**
     * 设置容量上限。
     * 插件被取下导致容量变小时，超出部分的能量会被丢弃，避免出现「存量大于上限」。
     */
    public void setCapacity(int capacity) {
        this.capacity = Math.max(1, capacity);
        if (this.energy > this.capacity) {
            this.energy = this.capacity;
        }
    }

    @Override
    public List<IFactory<? extends IContainerAddon>> getContainerAddons() {
        List<IFactory<? extends IContainerAddon>> addons = new ArrayList<>(super.getContainerAddons());
        addons.add(() -> new IntReferenceHolderAddon(
                new FunctionReferenceHolder(this::setCapacity, this::getMaxEnergyStored)));
        return addons;
    }
}
