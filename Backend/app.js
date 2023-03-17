const express = require('express');
const path = require('path');
const cookieParser = require('cookie-parser');
const logger = require('morgan');
const axios = require('axios');
 
// Google API
Google_Key = 'AIzaSyDhiBViOxvSg8iqfevWDyR7QABtzu3JW1A'
// YELP API
YELP_Key='MJLoIJefxV3ENLyGQQzoCBb3eWSZrL1pGA1aNYhpqoVNjk2IOFJuoZS6D1z9uwqI8Hywh45utI3lO6n1wRIfo2ypGX5pw9XbdiSCkmyv5bieoOBF9sLV6YosASw5Y3Yx'

const app = express();
app.all('*', function (req, res, next) {
  res.header('Access-Control-Allow-Origin', '*');
  res.header('Access-Control-Allow-Headers', 'X-Requested-With');
  res.header('Access-Control-Allow-Methods', 'PUT,POST,GET,DELETE,OPTIONS');
  res.header('X-Powered-By', ' 3.2.1');
  res.header('Content-Type', 'application/json;charset=utf-8');
  next(); 
});

// view engine setup
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'jade');

app.use(logger('dev'));
app.use(express.json());
app.use(express.urlencoded({ extended: false }));
app.use(cookieParser());
//app.use(express.static(path.join(__dirname, 'public')));

module.exports = app;

app.get('/', (req, res) => {
  res.send('Hello from App Engine!');
});

app.get('/atcpt', async function (req, res) {
  result = {suggestions: []}
  typedWord = req.query['typedWord'];
  
  // check if typedWord is empty string
  if (typedWord!=""){
    respsonse = await axios.get('https://api.yelp.com/v3/autocomplete',
    {
      headers: {'Authorization':'bearer ' + YELP_Key},
      params: {"text":typedWord}
    })
    .catch(err => {
      console.log('err:',err);
    });
  
    categories_resp = respsonse.data.categories
    terms_resp = respsonse.data.terms
    for (let i=0; i<categories_resp.length;i++) result.suggestions.push(categories_resp[i].title);
    for (let i=0; i<terms_resp.length;i++) result.suggestions.push(terms_resp[i].text);
  }
  res.send(JSON.stringify(result));
  res.end()
});

app.get('/search', async function (req, res) {
  // Receive information
  keyword = req.query['keyword_val'];
  distance = req.query['distance_val'];
  categoary = req.query['categoary_val'];
  //categoary = req.query['categoary_val'].replace('%26', '&');
  location = req.query['location_val'];
  autoloc = req.query['autoloc'];
  latitude = req.query['latitude'];
  longitude = req.query['longitude'];
  distance = parseInt(parseFloat(distance)*1609.34)
  if (categoary == 'Default') categoary = 'all';
  if (autoloc == 'false'){
    googlemapres = await axios.get("https://maps.googleapis.com/maps/api/geocode/json?address=" + location + "&key=AIzaSyDhiBViOxvSg8iqfevWDyR7QABtzu3JW1A")
    latitude = googlemapres.data.results[0].geometry.location.lat;
    longitude = googlemapres.data.results[0].geometry.location.lng;
  }
  parameters = {"term":keyword,
                "latitude":latitude,
                "longitude":longitude,
                "categories":categoary,
                "radius":distance,
                "limit":30}
  respsonse = await axios.get('https://api.yelp.com/v3/businesses/search',{
    headers: {'Authorization':'bearer ' + YELP_Key},
    params: parameters
  })
  
  result = {};
  var i = 0;
  var limit = respsonse.data.businesses.length;
  var index = 0
  while ((i < 10) && (i < limit)){
    if (! respsonse.data.businesses[i]['image_url'])continue;
    else if (! respsonse.data.businesses[i]['name']) continue;
    else if (! respsonse.data.businesses[i]['rating']) continue;
    else if (! respsonse.data.businesses[i]['distance']) continue;
    else {
      result[index] = {}
      result[index]['image_url'] = respsonse.data.businesses[i]['image_url'];
      result[index]['name'] = respsonse.data.businesses[i]['name'];
      result[index]['rating'] = respsonse.data.businesses[i]['rating'];
      result[index]['distance'] = (parseInt(parseFloat(respsonse.data.businesses[i]['distance'])/1609.34)).toString();
      result[index]['id'] = respsonse.data.businesses[i]['id'];
      index += 1;
    }
    i += 1;
  }
  res.send(JSON.stringify(result));
  res.end()
});

app.get('/searchdetail', async function (req, res) {
  // Receive information
  id = req.query['id'];
  
  // send request
  businessDetailEndpoint = 'https://api.yelp.com/v3/businesses/'+id;
  respsonse = await axios.get(businessDetailEndpoint,{
    headers: {'Authorization':'bearer ' + YELP_Key},
  })

  // Extract data from response
  Name = respsonse.data.name;
  Status = respsonse.data.hours[0].is_open_now;
  Address = respsonse.data.location.display_address.join();
  Category = '';
  for (let i=0; i<respsonse.data.categories.length; i++){
    Category += respsonse.data.categories[i].alias;
    Category += ' | ';
  }
  Category = Category.substring(0,Category.length-3);
  Transaction_Supported = respsonse.data.transactions.join(', ');
  Phone_number = respsonse.data.display_phone;
  Price = respsonse.data.price;
  More_info = respsonse.data.url;
  Photos = respsonse.data.photos;
  Coord = respsonse.data.coordinates;

  businessReviewEndpoint = "https://api.yelp.com/v3/businesses/" + id + "/reviews";
  reviewRespsonse = await axios.get(businessReviewEndpoint,{
    headers: {'Authorization':'bearer ' + YELP_Key},
  })

  result = {};

  if (!Name) result['Name'] = "N/A";
  else result['Name'] = Name;
  if (Status == null) result['Status'] = "N/A";
  else result['Status'] = Status;
  if (!Address)result['Address'] = "N/A";
  else result['Address'] = Address;
  if (!Category)result['Category'] = "N/A";
  else result['Category'] = Category;
  if (!Transaction_Supported)result['Transaction_Supported'] = "N/A";
  else result['Transaction_Supported'] = Transaction_Supported;
  if (!Phone_number)result['Phone_number'] = "N/A";
  else result['Phone_number'] = Phone_number;
  if (!Price) result['Price'] = "N/A";
  else result['Price'] = Price;
  if (!More_info) result['More_info'] = "N/A";
  else result['More_info'] = More_info;
  result['Photos'] = Photos;
  result['Coord'] = Coord;
  result['Reviews'] = reviewRespsonse.data.reviews;
  result['ReviewsAct'] = reviewRespsonse.data.reviews.length;

  res.send(JSON.stringify(result));
  res.end()
});

// var server = app.listen(80, function () {
//   var host = server.address().address
//   var port = server.address().port
//   console.log("Example app listening at http://%s:%s", host, port)
// });

// Running PORT is set automatically by App Engine
const port = process.env.PORT || 8080;
app.listen(port, () => {
  console.log(`Server is up on ${port}`);
});
