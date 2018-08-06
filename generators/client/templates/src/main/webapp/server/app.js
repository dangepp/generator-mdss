const express = require('express');
const proxy = require('http-proxy-middleware');
const formidable = require('formidable');
const fs = require('fs');
const app = express();

const filePath = '../webapp';
const imgFilePath = filePath + '/static/img/features/';

app.put(/\/feature/, function (req, res) {
  let form = new formidable.IncomingForm();
  form.parse(req, function (err, fields, files) {
    if (err) throw err;
    if (!fs.existsSync(imgFilePath)){
      fs.mkdirSync(imgFilePath);
	  }
    if (files.featureFile && fields.fileName) {
      let oldpath = files.featureFile.path
      let newpath = imgFilePath + fields.fileName
      fs.rename(oldpath, newpath, function (err) {
        if (err) throw err;
        res.send();
      });
    }
  })
});


app.use('/api', proxy({target: 'http://localhost:8090', changeOrigin: true}));

app.use(express.static(filePath));


app.listen(3000, function () {
  console.log('App listening on port 3000!');
});
