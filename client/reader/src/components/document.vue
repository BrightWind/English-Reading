<template>
  <div class="document">

    <div class="message-box" v-bind:class="{ 'hide-block': wordRemoved.word == null }">
      <p class="remove-block-tips">Do you want to remove "{{wordRemoved.word}}"</p>
      <div class="remove-block-actions"><button @click="OnConfirmRemove(wordRemoved)">Remove</button> <button @click="OnAbortRemove()">abort</button></div>
    </div>

    <div class="new-strange-word-list" v-bind:class="{ 'hide-block': new_strange_word_List.length == 0 }">
      <div class="new-strange-word" v-bind:class="{'select-strange-word': true }" v-for="word in new_strange_word_List" @dblclick="AddNewStrangeWord(word)"><span>{{word}}</span></div>
      <div class="word-list-done"><button @click="OnClickWordListDone()">Done</button></div>
    </div>

    <div class="title" @click="OnClickTitle()"><label>{{document.fileName}}</label></div>
    <div class="content">
      <div class="content-left" @scroll="OnScroll(event)">
        <label v-for="line in document.contentLines" @dblclick="OnDoubleClickDoc(line)"> {{line}}</label>
      </div>
      <div class="content-right">
        <div class="word-block" v-for="strangeWord in StrangeWordList">
          <div class="remove-block" @click="OnClickRemoveWordBlock(strangeWord)">X</div>
          <p class="word">{{strangeWord.word}}</p>
          <p class="speech">
            <span @click="OnClickSpeech(strangeWord.ukspeech)">{{'[' + strangeWord.ukphone + ']'}}</span>
            <span @click="OnClickSpeech(strangeWord.usspeech)">{{'[' + strangeWord.usphone + ']'}}</span>
          </p>
          <p v-for="explain in strangeWord.explain_list"> {{explain}}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
  .select-strange-word {
    background-color: goldenrod;
  }

  .hide-block {
    visibility: hidden;
  }

  .remove-block-tips {
    font-size: 25px;
    color: white;
    z-index: 1000;
  }

  .new-strange-word-list {
    display: flex;
    flex-wrap: wrap;
    align-content: flex-start;
    width: 400px;
    height: 250px;
    position: fixed;
    z-index: 1000;
    top: 25%;
    left: 10%;
    overflow-y: scroll;
    background-color: black;
  }

  .new-strange-word {
    width: 110px;
    height: 40px;
    margin: 5px 5px;
    background: burlywood;
  }
  .new-strange-word span {
    display: block;
    position: absolute;
    margin: auto;
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
    margin-top: 5px;
    background-color: burlywood;
    padding: 4px 4px;
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
  .content-left {
    position: relative;
    display: block;
    margin-top: 42px;
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

  export default {
    name: 'document',
    props: ['id', 'profile'],
    data () {
      return {
        document: {
          filename: '',
          contentLines: '',
        },
        StrangeWordList: [],
        new_strange_word_List:[],
        new_strange_word_List_status: [],
        wordRemoved: {},
      }
    },
    mounted: function () {
      this.OnClickTitle()
    },
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
      OnClickTitle () {
        let docId = ''
        if (this.$props.profile) {
          docId = this.$props.id
          this.$session.set('profile', docId)
        } else {
          docId = this.$session.get('profile')
        }

        let outerThis = this
        axios.get('/document/get?id=' + docId)
          .then(function (response) {
            outerThis.document = response.data
          })
          .catch(function (error) {
            console.log(error)
          })

        axios.get('/document/explain/get?doc_id=' + docId)
          .then(function (response) {
            outerThis.StrangeWordList = response.data
          })
          .catch(function (error) {
            console.log(error)
          })
      },
      OnClickSpeech(phone) {
      },
      OnClickRemoveWordBlock(word) {
        console.log(word)
        this.wordRemoved = word;
      },
      OnAbortRemove() {
        this.wordRemoved = {};
      },
      OnConfirmRemove(word) {
        let _this = this;
        axios.get(
          '/document/strange/word/delete', {
            params: {
              doc_id: _this.document.id,
              word: word.word }
          })
          .then(function (response) {
            var index = _this.StrangeWordList.indexOf(word);
            if (index > -1) {
              _this.StrangeWordList.splice(index, 1);
            }
          })
          .catch(function (error) {
            console.log(error)
          });
        this.wordRemoved = {};
      },
      AddNewStrangeWord(word) {
        let _this = this;
        _this.new_strange_word_List_status.push(word);
        axios.get(
          'document/strange/word/add', {
            params: {
              doc_id: _this.document.id,
              word: word }
          });
      },
      OnClickWordListDone() {
        this.new_strange_word_List = [];
        this.new_strange_word_List_status = [];
      },
      hasChineseLetter(str) {
        if (/.*[\u4e00-\u9fa5]+.*/.test(str)) {
          return true;
        } else {
          return false;
        }
      },
      OnDoubleClickDoc(line) {
        if (this.hasChineseLetter(line)) {
          return;
        }

        this.new_strange_word_List = line.split(' ');
      },
      OnScroll(event) {
        return;
      }
     }
  }
</script>
