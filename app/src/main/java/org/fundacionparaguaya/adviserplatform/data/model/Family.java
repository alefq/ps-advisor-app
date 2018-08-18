package org.fundacionparaguaya.adviserplatform.data.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.fundacionparaguaya.adviserplatform.data.local.Converters;

import java.util.Date;

/**
 * A family is the entity that is being helped by the advisor.
 */

@Entity(tableName = "families",
        indices={@Index(value="remote_id", unique=true)})
@TypeConverters(Converters.class)
public class Family {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "remote_id")
    private Long remoteId;
    @ColumnInfo(name = "name")
    private String name;
    @ColumnInfo(name = "code")
    private String code;
    @ColumnInfo(name = "address")
    private String address;
    @ColumnInfo(name="is_active")
    private boolean isActive;
    @ColumnInfo(name="last_modified")
    private Date lastModified;
    @Embedded
    private Location location;
    @Embedded(prefix = "family_member_")
    private FamilyMember member;
    @ColumnInfo(name="image_url")
    private String imageUrl;

    /**
     * Creates a new family. You should use the {@link Family#builder()} to construct a new family instead.
     */
    public Family(int id,
                  Long remoteId,
                  @NonNull String name,
                  @NonNull String code,
                  String address,
                  Location location,
                  FamilyMember member,
                  String imageUrl,
                  boolean isActive,
                  Date lastModified) {
        this.id = id;
        this.remoteId = remoteId;
        this.name = name;
        this.code = code;
        this.address = address;
        this.location = location;
        this.member = member;
        this.imageUrl = imageUrl;
        this.isActive = isActive;
        this.lastModified = lastModified;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Long getRemoteId() {
        return remoteId;
    }

    public void setRemoteId(Long remoteId) {
        this.remoteId = remoteId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLocation(Location location) {this.location = location; }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public FamilyMember getMember() {
        return member;
    }

    public String getAddress() {
        return address;
    }

    public Location getLocation() {
        return location;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    public Date getLastModified() { return lastModified; }

    public boolean isActive() {
        return isActive;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Family that = (Family) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(remoteId, that.remoteId)
                .append(name, that.name)
                .append(code, that.code)
                .append(address, that.address)
                .append(lastModified, that.lastModified)
                .append(isActive, that.isActive)
                .append(location, that.location)
                .append(member, that.member)
                .append(imageUrl, that.imageUrl)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(7, 11)
                .append(id)
                .append(remoteId)
                .append(name)
                .append(code)
                .append(address)
                .append(lastModified)
                .append(isActive)
                .append(location)
                .append(member)
                .append(imageUrl)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("remoteId", remoteId)
                .append("name", name)
                .append("code", code)
                .append("address", address)
                .append("isActive", isActive)
                .append("lastModified", lastModified)
                .append("location", location)
                .append("member", member)
                .append("imageUrl", imageUrl)
                .toString();
    }

    public static class Builder {
        private int id;
        private Long remoteId;
        private String name;
        private String code;
        private String address;
        private Location location;
        private FamilyMember member;
        private boolean isActive = true;
        private Date lastModified;
        private String imageUrl;

        /**
         * Sets the ID of the new family. Should only be used if overwriting an existing family!
         */
        public Builder id(int id) {
            this.id = id;
            return this;
        }

        public Builder remoteId(Long remoteId) {
            this.remoteId = remoteId;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder code(String code) {
            this.code = code;
            return this;
        }

        public Builder address(String address) {
            this.address = address;
            return this;
        }

        public Builder location(Location location) {
            this.location = location;
            return this;
        }

        public Builder member(FamilyMember member) {
            this.member = member;
            return this;
        }

        public Builder imageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
            return this;
        }

        public Builder isActive(boolean isActive) {
            this.isActive = isActive;
            return this;
        }

        public Builder lastModified(Date lastModified) {
            this.lastModified = lastModified;
            return this;
        }

        /**
         * Fills in the information about a family that is available from a snapshot's personal
         * responses.
         */
        public Builder snapshot(Snapshot snapshot) {
            FamilyMember member = FamilyMember.builder().snapshot(snapshot).build();
            member(member);
            name(member.getFirstName() + " " + member.getLastName());
            code(member.getCountryOfBirth()
                    + "."
                    + member.getFirstName().toUpperCase().charAt(0)
                    + member.getLastName().toUpperCase().charAt(0)
                    + "."
                    + member.getBirthdate().replace("-", ""));
            lastModified(snapshot.getCreatedAt());
            return this;
        }

        public Family build() {
            return new Family(id, remoteId, name, code, address, location, member, imageUrl, isActive, lastModified);
        }
    }


}
