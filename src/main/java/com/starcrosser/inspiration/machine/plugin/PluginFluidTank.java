package com.starcrosser.inspiration.machine.plugin;

import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.component.IComponentHarness;
import com.hrznstudio.titanium.component.fluid.SidedFluidTankComponent;
import com.hrznstudio.titanium.container.addon.IContainerAddon;
import com.hrznstudio.titanium.container.addon.IntReferenceHolderAddon;
import com.hrznstudio.titanium.container.referenceholder.FunctionReferenceHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * 容量可被插件改写的罐体（灵感充能罐用）。
 * <p>
 * 基类只会把「流体种类 + 存量」同步给客户端，容量不在其中；客户端那一份罐体的容量因此始终
 * 是构造值。灵感插件把服务端容量翻倍后，客户端算出的液位比例就是 存量 / 旧容量，
 * 液位会画到槽位外面去。这里在基类的同步项之后再补一条容量的 {@code DataSlot}。
 */
public class PluginFluidTank<T extends IComponentHarness> extends SidedFluidTankComponent<T> {

    public PluginFluidTank(String name, int capacity, int x, int y, int facingPos) {
        super(name, capacity, x, y, facingPos);
    }

    /**
     * 补一条同步容量的 {@code DataSlot}。
     * <p>
     * 写回时用 {@code FluidTank#setCapacity} 本身即可：它只改容量、不动存量，
     * 而存量由 {@code FluidTankReferenceHolder} 自己同步，两边各自到位。
     */
    @Override
    public List<IFactory<? extends IContainerAddon>> getContainerAddons() {
        List<IFactory<? extends IContainerAddon>> addons = new ArrayList<>(super.getContainerAddons());
        addons.add(() -> new IntReferenceHolderAddon(
                new FunctionReferenceHolder(value -> setCapacity(Math.max(1, value)), this::getCapacity)));
        return addons;
    }
}
