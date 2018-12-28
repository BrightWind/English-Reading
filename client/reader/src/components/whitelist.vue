<template>
  <div class="document" id="document">
    
    <div class="message-box" v-show="show_dialog">
      <p class="remove-block-tips">{{message}}</p>
      <div class="remove-block-actions"><button @click="OnClickDialogOK()">OK</button> <button @click="OnClickDialogCancel()">abort</button></div>
    </div>

    <div class="title"><label>{{document.fileName}}</label><button @click="OnClickSubmit()">Submit<button</div>

    <div class="content" id="doc-content">
      <div class="content-left" id="doc-content-left">
        <label class="document-line" v-for="(line, index) in document.contentLines" >
          <span class="document-line-word" v-for="word in line.split(' ')" @click="AddToWhiteList(word)">{{word}}</span>
        </label>
      </div>
      <div class="content-right" id="doc-content-right">
        <div class="word-block" v-for="strangeWord in visible_strange_word_list" >
          <div class="remove-block" @click="OnClickRemoveWordBlock(strangeWord)">X</div>
          <p class="word">{{strangeWord}}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
  .document {
    position: absolute;
    width: 100%;
    top: 42px;
    bottom: 10px;
    overflow-y: auto;
  }

  .hide-block {
    visibility: hidden;
  }

  .remove-block-tips {
    font-size: 25px;
    color: white;
    z-index: 1000;
  }

  .word-list-done button {
    width: 80px;
  }
  .word-list-done {
    position: absolute;
    bottom: 10px;
    left: 50%;
    margin-left: -40px;
  }

  .remove-block-actions {
    position: absolute;
    bottom: 20px;
    left: 50%;
    margin-left: -85px;
  }

  .remove-block-actions button {
    width: 80px;
    margin-right:5px;
  }

  .message-box {
    width: 50%;
    height: 100px;
    position: fixed;
    z-index: 1000;
    top: 25%;
    left: 25%;
    background-color: black;
  }


  .remove-block {
    right: 8px;
    width: 40px;
    height: 30px;
    position: absolute;
    background-color: aliceblue;
    font-size: 30px;

  }
  .word-block {
    position: relative;
    margin-top: 5px;
    background-color: burlywood;
    padding: 4px 4px;
  }

  .video-block {
    display: none;
  }

  .video-trigger {
    position: absolute;
    left: 0;
    right: 0;
    bottom: 0;
    top: 35px;
    z-index: 1000;
  }

  .word {
    margin-top: 0px;
    font-size: 23px;
    margin-bottom: 10px;
    line-height: 20px;
  }

  p {
    margin-bottom: 0px;
    line-height: 15px;
    font-size: 14px;
    margin-top: 10px;
  }
  .speech {
  }

  .title {
    position: fixed;
    display: block;
    width: 100%;
    height: 40px;
    top: 0;
    left: 0;
    z-index: 1000;
    background-color: bisque;
    text-align: center;
  }
  .title label {
    position: relative;
    top: 10px;
  }
  .content {
    position: relative;
    width: 100%;
  }

  .document-line-word {
    display: inline;
    padding-right: 5px;
  }
  .content-left {
    position: relative;
    display: block;
    margin-left: 0;
    left: 0;
    width: 70%;
    padding: 0 5px;
    background-color: white;
  }
  .content-right {
    position: fixed;
    right: 0;
    top: 43px;
    bottom: 2px;
    display: block;
    width: 28%;
    padding: 0 5px;
    text-align: left;
    background-color: red;
    overflow-y: scroll;
  }
  h1, h2 {
    font-weight: normal;
  }
  ul {
    list-style-type: none;
    padding: 0;
  }
  li {
    display: inline-block;
    margin: 0 10px;
  }
  a {
    color: #42b983;
  }

  .content-left label {
    display: inline-block;
    width: 100%;
    text-align: left;
    font-size: 20px;
    line-height: 33px;
    margin: 2px 0;
    background-color: coral;
  }
</style>

<script>
  import { mapActions } from 'vuex'
  import axios from 'axios'
  import math from 'math'

  export default {
    name: 'whitelist',
    props: ['id', 'profile'],
    data () {
      return {
        document: {
          filename: '',
          contentLines: '',
        },
        visible_strange_word_list: [],
        show_dialog: false,
        message: 'Submit the white list'
      }
    },
    mounted: function () {
      let _this = this;
      this.OnLoadDocument()
    },
    // updated: function(){
    //   this.$el.scrollTop = this.document.rPosition;
    // },
    // beforeDestroy() {
    //   let _this = this;
    //     console.log("reject left scroll..");
    // },
    computed: {
      /*
      document () {
        let doc = this.$store.state.doc_list[this.$props.profile]
        console.log(this.$store.state)
        if (typeof doc === 'undefined' || doc === null) {
          doc = {
            title: 'Document not found'
          }
        }
        doc = {
          title: 'Document not found'
        }
        return doc
      }
      */

    },
    methods: {
      ...mapActions(['get_document_list']),
      OnClickSubmit() {
        this.show_dialog = true;
      },
      OnClickDialogOK() {
        this.show_dialog = false;
        let white_set = new Set();
        this.visible_strange_word_list.forEach(item => {
          white_set.add(item)
        });

        let docId = ''
        if (this.$props.profile) {
          docId = this.$props.id
          this.$session.set('profile', docId)
        } else {
          docId = this.$session.get('profile')
        }

        let WhiteListParam = {
            doc_id: docId,
            white_list = white_set
        }
        axios.post('list/white/list/add', WhiteListPara)
          .then(function (response) {
            _this.document = response.data
          })
          .catch(function (error) {
            console.log(error)
          })

      },
      OnClickDialogCancel() {
        this.show_dialog = false;
      },
      OnLoadDocument () {
        let docId = ''
        if (this.$props.profile) {
          docId = this.$props.id
          this.$session.set('profile', docId)
        } else {
          docId = this.$session.get('profile')
        }

        let _this = this
        axios.get('/document/get?id=' + docId)
          .then(function (response) {
            _this.document = response.data
          })
          .catch(function (error) {
            console.log(error)
          })
      },
      AddToWhiteList(word) {
        let _this = this;
        _this.visible_strange_word_list.push(word);
      },
      OnClickRemoveWordBlock(word) {
        console.log(word);
        let idx = this.visible_strange_word_list.indexOf(word);
        if (idx != -1) {
          this.visible_strange_word_list.splice(idx, 1);
        }
      }
    }
  }
</script>
