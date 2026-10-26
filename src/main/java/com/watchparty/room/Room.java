package com.watchparty.room;
import com.watchparty.auth.User; import jakarta.persistence.*; import java.time.Instant; import java.util.*;
@Entity
public class Room {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(unique=true,nullable=false,length=6) private String code;
 @Column(nullable=false,length=120) private String name;
 @ManyToOne(optional=false) private User host;
 @Column(length=1000) private String videoUrl;
 private boolean playing;
 private double positionSeconds;
 private Instant updatedAt=Instant.now();
 @ManyToMany
 @JoinTable(name="room_members",joinColumns=@JoinColumn(name="room_id"),inverseJoinColumns=@JoinColumn(name="user_id"))
 private Set<User> members=new HashSet<>();
 public Room(){}
 public Room(String code,String name,User host,String videoUrl){this.code=code;this.name=name;this.host=host;this.videoUrl=videoUrl;members.add(host);}
 public Long getId(){return id;} public String getCode(){return code;} public String getName(){return name;} public void setName(String v){name=v;}
 public User getHost(){return host;} public String getVideoUrl(){return videoUrl;} public void setVideoUrl(String v){videoUrl=v;}
 public boolean isPlaying(){return playing;} public void setPlaying(boolean v){playing=v;updatedAt=Instant.now();}
 public double getPositionSeconds(){return positionSeconds;} public void setPositionSeconds(double v){positionSeconds=v;updatedAt=Instant.now();}
 public Instant getUpdatedAt(){return updatedAt;} public Set<User> getMembers(){return members;}
 public int getMemberCount(){return members.size();}
 public boolean hasMember(User u){return u!=null&&members.stream().anyMatch(x->x.getId().equals(u.getId()));}
 public boolean isHost(User u){return u!=null&&host.getId().equals(u.getId());}
}
