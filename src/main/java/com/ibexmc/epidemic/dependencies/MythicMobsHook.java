package com.ibexmc.epidemic.dependencies;

import com.ibexmc.epidemic.Epidemic;
import com.ibexmc.epidemic.ailments.AfflictPlayer;
import com.ibexmc.epidemic.ailments.Afflicted;
import com.ibexmc.epidemic.ailments.Ailment;
import com.ibexmc.epidemic.ailments.AilmentManager;
import com.ibexmc.epidemic.util.Logging;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

public class MythicMobsHook {

    private static Object mobManager;
    private static Method isMythicMobMethod;
    private static Method getMythicMobInstanceMethod;
    private static Method getTypeMethod;

    static {
        try {
            Class<?> mythicBukkitClass = Class.forName("io.lumine.mythic.bukkit.MythicBukkit");
            Method instMethod = mythicBukkitClass.getMethod("inst");
            Object inst = instMethod.invoke(null);
            Method getMobManagerMethod = inst.getClass().getMethod("getMobManager");
            mobManager = getMobManagerMethod.invoke(inst);

            isMythicMobMethod = mobManager.getClass().getMethod("isMythicMob", Entity.class);
            getMythicMobInstanceMethod = mobManager.getClass().getMethod("getMythicMobInstance", Entity.class);
            
            // For MythicMob instance
            // getType() returns the MythicMob type
        } catch (Exception ignored) {
            // MythicMobs not present or version mismatch
        }
    }

    public static void register(Epidemic plugin) {
        if (mobManager == null) return;

        try {
            Bukkit.getPluginManager().registerEvents(new MythicLoader(), plugin);
        } catch (Exception e) {
            Logging.debug("MythicMobsHook", "register", "Error registering MythicMobs loader: " + e.getMessage());
        }
    }

    public static class MythicLoader implements Listener {

        @EventHandler
        public void onMythicMechanicLoad(org.bukkit.event.Event event) {
            if (event.getClass().getName().endsWith("MythicMechanicLoadEvent")) {
                try {
                    Method getMechanicName = event.getClass().getMethod("getMechanicName");
                    String mechanicName = (String) getMechanicName.invoke(event);

                    if (mechanicName.equalsIgnoreCase("epidemicAfflict")) {
                        Method getConfig = event.getClass().getMethod("getConfig");
                        Object config = getConfig.invoke(event);
                        
                        Class<?> mechanicInterface = Class.forName("io.lumine.mythic.api.skills.ITargetedEntitySkill");
                        Object mechanicProxy = Proxy.newProxyInstance(
                                mechanicInterface.getClassLoader(),
                                new Class<?>[]{mechanicInterface},
                                new EpidemicAfflictSkillProxy(config)
                        );
                        
                        Method registerMethod = event.getClass().getMethod("register", mechanicInterface);
                        registerMethod.invoke(event, mechanicProxy);
                    }
                } catch (Exception e) {
                    Logging.debug("MythicMobsHook", "onMythicMechanicLoad", "Error: " + e.getMessage());
                }
            } else if (event.getClass().getName().endsWith("MythicConditionLoadEvent")) {
                try {
                    Method getConditionName = event.getClass().getMethod("getConditionName");
                    String conditionName = (String) getConditionName.invoke(event);

                    if (conditionName.equalsIgnoreCase("hasAilment")) {
                        Method getConfig = event.getClass().getMethod("getConfig");
                        Object config = getConfig.invoke(event);

                        Class<?> conditionInterface = Class.forName("io.lumine.mythic.api.skills.conditions.IEntityCondition");
                        Object conditionProxy = Proxy.newProxyInstance(
                                conditionInterface.getClassLoader(),
                                new Class<?>[]{conditionInterface},
                                new HasAilmentConditionProxy(config)
                        );

                        Method registerMethod = event.getClass().getMethod("register", conditionInterface);
                        registerMethod.invoke(event, conditionProxy);
                    }
                } catch (Exception e) {
                    Logging.debug("MythicMobsHook", "onMythicConditionLoad", "Error: " + e.getMessage());
                }
            }
        }
    }

    private static class EpidemicAfflictSkillProxy implements InvocationHandler {
        private final String ailmentName;
        private final double chance;
        private final Random random = new Random();

        public EpidemicAfflictSkillProxy(Object config) throws Exception {
            Method getString = config.getClass().getMethod("getString", String[].class, String.class);
            Method getDouble = config.getClass().getMethod("getDouble", String[].class, double.class);
            
            this.ailmentName = (String) getString.invoke(config, new String[]{"ailment", "a"}, "");
            this.chance = (double) getDouble.invoke(config, new String[]{"chance", "c"}, 1.0);
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (method.getName().equals("castAtEntity")) {
                // ITargetedEntitySkill.castAtEntity(SkillMetadata data, AbstractEntity target)
                Object target = args[1];
                if (target == null) return null; // SkillResult.INVALID_TARGET handled by reflection if needed

                if (random.nextDouble() > chance) return null; // SkillResult.CONDITION_FAILED

                Ailment ailment = Epidemic.instance().data().getAvailableAilmentByInternalName(ailmentName);
                if (ailment != null) {
                    Method getBukkitEntity = target.getClass().getMethod("getBukkitEntity");
                    Entity bukkitEntity = (Entity) getBukkitEntity.invoke(target);
                    
                    Object data = args[0];
                    Method getCaster = data.getClass().getMethod("getCaster");
                    Object caster = getCaster.invoke(data);
                    Method getEntity = caster.getClass().getMethod("getEntity");
                    Object casterEntity = getEntity.invoke(caster);
                    Method getUniqueId = casterEntity.getClass().getMethod("getUniqueId");
                    UUID casterUUID = (UUID) getUniqueId.invoke(casterEntity);

                    AfflictPlayer.afflictPlayer(bukkitEntity.getUniqueId(), ailment, casterUUID, null);
                }
                
                // Return SkillResult.SUCCESS
                Class<?> skillResultClass = Class.forName("io.lumine.mythic.api.skills.SkillResult");
                return Enum.valueOf((Class<Enum>) skillResultClass, "SUCCESS");
            }
            return null;
        }
    }

    private static class HasAilmentConditionProxy implements InvocationHandler {
        private final String ailmentName;

        public HasAilmentConditionProxy(Object config) throws Exception {
            Method getString = config.getClass().getMethod("getString", String[].class, String.class);
            this.ailmentName = (String) getString.invoke(config, new String[]{"ailment", "a"}, "");
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (method.getName().equals("check")) {
                // IEntityCondition.check(AbstractEntity entity)
                Object entity = args[0];
                if (entity == null) return false;

                Method getUniqueId = entity.getClass().getMethod("getUniqueId");
                UUID uuid = (UUID) getUniqueId.invoke(entity);

                Set<Afflicted> afflictions = Epidemic.instance().data().getPlayerAfflictionsByUUID(uuid);
                if (afflictions != null) {
                    for (Afflicted affliction : afflictions) {
                        if (affliction.getAilment().getInternalName().equalsIgnoreCase(ailmentName)) {
                            return true;
                        }
                    }
                }
                return false;
            }
            return null;
        }
    }

    public static boolean isMythicMob(Entity entity) {
        if (mobManager == null || isMythicMobMethod == null) return false;
        try {
            return (boolean) isMythicMobMethod.invoke(mobManager, entity);
        } catch (Exception e) {
            return false;
        }
    }

    public static String getMythicMobType(Entity entity) {
        if (mobManager == null || getMythicMobInstanceMethod == null) return null;
        try {
            Object mobInstance = getMythicMobInstanceMethod.invoke(mobManager, entity);
            if (mobInstance != null) {
                if (getTypeMethod == null) {
                    getTypeMethod = mobInstance.getClass().getMethod("getType");
                }
                Object type = getTypeMethod.invoke(mobInstance);
                if (type != null) {
                    Method getInternalNameMethod = type.getClass().getMethod("getInternalName");
                    return (String) getInternalNameMethod.invoke(type);
                }
            }
        } catch (Exception e) {
            Logging.debug("MythicMobsHook", "getMythicMobType", "Error: " + e.getMessage());
        }
        return null;
    }
}
