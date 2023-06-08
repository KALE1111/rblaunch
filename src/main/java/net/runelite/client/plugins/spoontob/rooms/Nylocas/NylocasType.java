package net.runelite.client.plugins.spoontob.rooms.Nylocas;

import java.util.HashMap;

enum NylocasType {
    MELEE_SMALL(8342, 8348),
    MELEE_BIG(8345, 8351),
    RANGE_SMALL(8343, 8349),
    RANGE_BIG(8346, 8352),
    MAGE_SMALL(8344, 8350),
    MAGE_BIG(8347, 8353),

    SM_MELEE_SMALL(10774, 10780), //Story Mode
    SM_MELEE_BIG(10777, 10783),
    SM_RANGE_SMALL(10775, 10781),
    SM_RANGE_BIG(10778, 10784),
    SM_MAGE_SMALL(10776, 10782),
    SM_MAGE_BIG(10779, 10785),

    HM_MELEE_SMALL(10791, 10797), // Hard Mode
    HM_MELEE_BIG(10794, 10800),
    HM_RANGE_SMALL(10792, 10798),
    HM_RANGE_BIG(10795, 10801),
    HM_MAGE_SMALL(10793, 10799),
    HM_MAGE_BIG(10796, 10802);

    private int id;
    private int aggroId;
    private static final HashMap<Integer, NylocasType> lookupMap = new HashMap();

    private NylocasType(int id, int aggroId) {
        this.id = id;
        this.aggroId = aggroId;
    }

    public int getId() {
        return this.id;
    }

    public int getAggroId() {
        return this.aggroId;
    }

    public static HashMap<Integer, NylocasType> getLookupMap() {
        return lookupMap;
    }

    static {
        NylocasType[] var0 = values();
        int var1 = var0.length;

        for(int var2 = 0; var2 < var1; ++var2) {
            NylocasType v = var0[var2];
            lookupMap.put(v.getId(), v);
            lookupMap.put(v.getAggroId(), v);
        }

    }
}