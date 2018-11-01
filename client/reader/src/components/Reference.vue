<template>
  <div>
    <label>this is reference page</label>
    <div class="document-list">
      <li v-for="doc in docs">
        <div class="document-item">
          <label>doc.title</label>
        </div>
      </li>
    </div>
    <div>
      <footer></footer>
    </div>
  </div>
</template>
<script>
export default {
  name: 'Reference',
  data () {
    return {
      docs: [{title: 'doc1'}, {title: 'doc2'}]
    }
  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
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
</style>
