<template>
    <div id="content">
        <br />
        <h3 style="margin-bottom: 2%;">{{ name(extractFileName(videoUrl)) }}</h3>
        <div class="video-container">
            <div class="container-fluid " style="width: 85%;">

                <!-- 一般影片（非 YouTube） -->
                <video ref="videoPlayer" class="video-js vjs-default-skin" controls preload="auto" data-setup="{}"
                    @play="isPlaying = true" @pause="isPlaying = false">

                    <!-- <source :src="'/vocal' + videoUrl" type="video/mp4" /> -->
                    <source
                        :src="'https://tsl.ccu.edu.tw/vocal/hls/' + videoTitle(extractFileName(videoUrl)) + '/playlist.m3u8'"
                        type="application/x-mpegURL" />
                    <p>無效的影片路徑</p>
                </video>
            </div>
        </div>

        <!-- 控制按鈕 -->
        <div class="video-controls">
            <button @click="playVideo" :disabled="isPlaying">播放</button>
            <button @click="pauseVideo" :disabled="!isPlaying">暫停</button>
            <!-- 🔙 返回按鈕 -->
            <button @click="goBackToHome">返回主頁</button>
        </div>
        <br />
    </div>
</template>

<script>
import instance from '@/utils/axios.js';
import videojs from 'video.js';
import 'video.js/dist/video-js.css'; // 引入 Video.js 樣式

export default {
    name: "VideoShow",
    props: {
        url: {
            type: String,
            required: true
        },
    },
    data() {
        return {
            videoUrl: "",
            isPlaying: false,
            player: null,
            word_id: "",
            video_number: "",
        };
    },
    mounted() {
        this.fetchVideo();
    },
    beforeUnmount() {
        if (this.player) {
            this.player.dispose(); // 銷毀播放器，避免記憶體洩漏
        }
    },
    computed: {
        decodedVideoUrl() {
            return decodeURIComponent(this.$route.params.video_url);
        }
    },
    methods: {
        name(videoName) {
            if (videoName) {
                // 去掉副檔名
                videoName = videoName.replace(/\.[^/.]+$/, "");
                // 將底線後面的字取出
                const parts = videoName.split('-');
                if (parts[1] === ("video1")) {
                    return parts[0] + '的例句1影片';
                }
                else if (parts[1] === ("video2")) {
                    return parts[0] + '的例句2影片';
                }
                else if (parts[1] === ("video")) {
                    return parts[0] + '的詞彙影片';
                }
                return videoName; // 如果沒有底線，直接返回原始名稱

            }
        },
        extractFileName(url) {
            if (!url) return '';
            const parts = url.split('/');
            return parts[parts.length - 1] || '未知影片';
        },
        videoTitle(filename) {
            // 去掉路徑，只取檔名
            const baseName = filename.split('/').pop();
            // 去掉副檔名
            return baseName.replace(/\.[^/.]+$/, "");
        },
        initVideoPlayer() {
            this.player = videojs(this.$refs.videoPlayer, {
                autoplay: false,
                controls: true,
                responsive: true,
                fluid: true,
                aspectRatio: '16:9',
                playbackRates: [0.5, 1, 1.5, 2],
            });

            this.player.on('play', () => console.log("播放"));
            this.player.on('pause', () => console.log("暫停"));
            this.player.on('ended', () => console.log("結束"));
            this.player.ready(() => {
                this.player.volume(1);
            });
        },

        goBackToHome() {
            const thId = localStorage.getItem('scrollToId'); // 之前點影片時存的 th_id
            this.$router.push({ path: '/home' });
        },

        playVideo() {
            this.$refs.videoPlayer.play();
            this.isPlaying = true;
        },
        pauseVideo() {
            this.$refs.videoPlayer.pause();
            this.isPlaying = false;
        },
        async fetchVideo() {
            const decoded = decodeURIComponent(this.url);

            this.videoUrl = `${decoded}`;
            const match = decoded.match(/\/[Vv]ideo\/([a-zA-Z0-9_]+)-([a-zA-Z0-9]+)\.\w+$/);
            if (match) {
                this.word_id = match[1];
                this.video_number = match[2];
                console.log("word_id:", this.word_id);
                console.log("video_number:", this.video_number);
                try {
                    const res = await instance.get(`/getVideo/${this.word_id}/${this.videoNumber}`);
                    this.videoUrl = res.data; // 後端傳回的 HLS 播放清單 URL
                    this.initVideoPlayer();
                } catch (err) {
                    console.error("取得影片失敗", err);
                }
            } else {
                console.log("無法解析 videoUrl");
            }

            this.$nextTick(() => {
                this.initVideoPlayer();
            });
        },
    },
};
</script>

<style scoped>
.video-container {
    display: flex;
    justify-content: center;
    align-items: center;
    margin: 0 auto;
    max-width: 80%;
    /* padding: 20px; */
    box-sizing: border-box;

    background: #f5f5f5;
    border-radius: 15px;
    box-shadow: 3px 3px 8px rgba(0, 0, 0, 0.1);
    /* 保證底部的灰框跟上左右的一樣寬 */
    padding-bottom: 5%;
    max-width: 70%;
    /* margin: 0 auto; */
    /* 在整個畫面置中 */
}

h3 {
    text-align: center;
    color: #4a4a4a;
    display: inline-flexbox;
}

.video-wrapper {
    display: flex;
    justify-content: center;
    align-items: center;
    background-color: #000;
    border-radius: 10px;
    overflow: hidden;
}

video {

    max-width: 100%;
    max-height: 100%;
    display: block;
    margin: auto;
}

.video-controls {
    display: flex;
    justify-content: center;
    gap: 20px;
    margin-top: 15px;
}

button {
    background-color: #4a90e2;
    color: #fff;
    border: none;
    padding: 10px 20px;
    border-radius: 5px;
    cursor: pointer;
    transition: background-color 0.3s ease-in-out;
}

button:hover {
    background-color: #357ab7;
}

button:disabled {
    background-color: #cccccc;
    cursor: not-allowed;
}

.error-message {
    text-align: center;
    color: red;
    font-weight: bold;
    /* margin-top: 20px; */
}

.row.align-items-stretch {
    display: flex;
    justify-content: center;
    align-items: center;
}
</style>
