const code=window.ROOM_CODE;
const isHost=window.IS_HOST===true;
const video=document.querySelector("#video");
const messages=document.querySelector("#messages");
let lastMessageId=0;
function csrf(){const token=document.cookie.split("; ").find(x=>x.startsWith("XSRF-TOKEN="));return token?decodeURIComponent(token.split("=")[1]):"";}
function headers(){return {"Content-Type":"application/json","X-XSRF-TOKEN":csrf()};}
function escapeHtml(value){return String(value).replace(/[&<>"']/g,c=>({"&":"&amp;","<":"&lt;",">":"&gt;",'"':"&quot;","'":"&#39;"}[c]));}
async function sync(){const response=await fetch(`/api/rooms/${code}/playback`);if(!response.ok){return;}const state=await response.json();document.querySelector("#status").textContent="Connected";if(video){if(Math.abs(video.currentTime-state.positionSeconds)>2)video.currentTime=state.positionSeconds;if(state.playing&&video.paused)video.play().catch(()=>{});if(!state.playing&&!video.paused)video.pause();}}
async function update(){if(!video||!isHost)return;const response=await fetch(`/api/rooms/${code}/playback`,{method:"POST",headers:headers(),body:JSON.stringify({playing:!video.paused,positionSeconds:video.currentTime})});if(!response.ok)document.querySelector("#status").textContent="Playback update failed";}
async function loadChat(){const response=await fetch(`/api/rooms/${code}/chat?afterId=${lastMessageId}`);if(!response.ok)return;const items=await response.json();for(const message of items){messages.insertAdjacentHTML("beforeend",`<div class="message"><b>${escapeHtml(message.author)}</b><span>${escapeHtml(new Date(message.sentAt).toLocaleTimeString())}</span><br>${escapeHtml(message.text)}</div>`);lastMessageId=Math.max(lastMessageId,message.id||0);}messages.scrollTop=messages.scrollHeight;}
async function loadMembers(){const response=await fetch(`/api/rooms/${code}/members`);if(!response.ok)return;const members=await response.json();document.querySelector("#members").innerHTML=members.map(member=>`<li>${escapeHtml(member.displayName)} <span class="muted">@${escapeHtml(member.username)}</span></li>`).join("");}
document.querySelector("#sync").onclick=sync;document.querySelector("#copy-code").onclick=()=>navigator.clipboard?.writeText(code).catch(()=>{});
if(video&&!isHost)video.controls=false;
if(video){video.addEventListener("play",update);video.addEventListener("pause",update);video.addEventListener("seeked",update);}
document.querySelector("#chat-form").onsubmit=async event=>{event.preventDefault();const input=document.querySelector("#chat-input");if(!input.value.trim())return;const response=await fetch(`/api/rooms/${code}/chat`,{method:"POST",headers:headers(),body:JSON.stringify({text:input.value})});if(response.ok){input.value="";await loadChat();}};
sync();loadChat();loadMembers();setInterval(sync,3000);setInterval(loadChat,3000);setInterval(loadMembers,5000);
