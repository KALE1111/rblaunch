package net.runelite.client.plugins.socketba.jacob;

import net.runelite.api.coords.WorldPoint;

public class GroundItem {
	private int id;

	private int itemId;

	private String name;

	private int quantity;

	private WorldPoint location;

	private int height;

	private int offset;

	GroundItem(int id, int itemId, String name, int quantity, WorldPoint location, int height, int offset) {
		this.id = id;
		this.itemId = itemId;
		this.name = name;
		this.quantity = quantity;
		this.location = location;
		this.height = height;
		this.offset = offset;
	}

	public static GroundItemBuilder builder() {
		return new GroundItemBuilder();
	}

	public int getId() {
		return this.id;
	}

	public int getItemId() {
		return this.itemId;
	}

	public String getName() {
		return this.name;
	}

	public int getQuantity() {
		return this.quantity;
	}

	public WorldPoint getLocation() {
		return this.location;
	}

	public int getHeight() {
		return this.height;
	}

	public int getOffset() {
		return this.offset;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setItemId(int itemId) {
		this.itemId = itemId;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public void setLocation(WorldPoint location) {
		this.location = location;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof GroundItem))
			return false;
		GroundItem other = (GroundItem)o;
		if (!other.canEqual(this))
			return false;
		if (getId() != other.getId())
			return false;
		if (getItemId() != other.getItemId())
			return false;
		Object this$name = getName();
		Object other$name = other.getName();
		if ((this$name == null) ? (
			other$name == null) :

			this$name.equals(other$name)) {
			if (getQuantity() != other.getQuantity())
				return false;
			Object this$location = getLocation();
			Object other$location = other.getLocation();
			if ((this$location == null) ? (
				other$location == null) :

				this$location.equals(other$location)) {
				if (getHeight() != other.getHeight())
					return false;
				return (getOffset() == other.getOffset());
			}
			return false;
		}
		return false;
	}

	protected boolean canEqual(Object other) {
		return other instanceof GroundItem;
	}

	public int hashCode() {
		boolean PRIME = true;
		int result = 1;
		result = result * 59 + getId();
		result = result * 59 + getItemId();
		Object $name = getName();
		result = result * 59 + (($name == null) ? 43 : $name.hashCode());
		result = result * 59 + getQuantity();
		Object $location = getLocation();
		result = result * 59 + (($location == null) ? 43 : $location.hashCode());
		result = result * 59 + getHeight();
		result = result * 59 + getOffset();
		return result;
	}

	public String toString() {
		return "GroundItem(id=" + getId() + ", itemId=" + getItemId() + ", name=" + getName() + ", quantity=" + getQuantity() + ", location=" + getLocation() + ", height=" + getHeight() + ", offset=" + getOffset() + ")";
	}

	public static class GroundItemBuilder {
		private int id;

		private int itemId;

		private String name;

		private int quantity;

		private WorldPoint location;

		private int height;

		private int offset;

		public GroundItemBuilder id(int id) {
			this.id = id;
			return this;
		}

		public GroundItemBuilder itemId(int itemId) {
			this.itemId = itemId;
			return this;
		}

		public GroundItemBuilder name(String name) {
			this.name = name;
			return this;
		}

		public GroundItemBuilder quantity(int quantity) {
			this.quantity = quantity;
			return this;
		}

		public GroundItemBuilder location(WorldPoint location) {
			this.location = location;
			return this;
		}

		public GroundItemBuilder height(int height) {
			this.height = height;
			return this;
		}

		public GroundItemBuilder offset(int offset) {
			this.offset = offset;
			return this;
		}

		public GroundItem build() {
			return new GroundItem(this.id, this.itemId, this.name, this.quantity, this.location, this.height, this.offset);
		}

		public String toString() {
			return "GroundItem.GroundItemBuilder(id=" + this.id + ", itemId=" + this.itemId + ", name=" + this.name + ", quantity=" + this.quantity + ", location=" + this.location + ", height=" + this.height + ", offset=" + this.offset + ")";
		}
	}

	public static final class GroundItemKey {
		private final int itemId;

		private final WorldPoint location;

		public GroundItemKey(int itemId, WorldPoint location) {
			this.itemId = itemId;
			this.location = location;
		}

		public int getItemId() {
			return this.itemId;
		}

		public WorldPoint getLocation() {
			return this.location;
		}

		public boolean equals(Object o) {
			if (o == this)
				return true;
			if (!(o instanceof GroundItemKey))
				return false;
			GroundItemKey other = (GroundItemKey)o;
			if (getItemId() != other.getItemId())
				return false;
			Object this$location = getLocation();
			Object other$location = other.getLocation();
			if (this$location == null) {
				if (other$location != null)
					return false;
			} else if (!this$location.equals(other$location)) {
				return false;
			}
			return true;
		}

		public int hashCode() {
			boolean PRIME = true;
			int result = 1;
			result = result * 59 + getItemId();
			Object $location = getLocation();
			result = result * 59 + (($location == null) ? 43 : $location.hashCode());
			return result;
		}

		public String toString() {
			return "GroundItem.GroundItemKey(itemId=" + getItemId() + ", location=" + getLocation() + ")";
		}
	}
}
