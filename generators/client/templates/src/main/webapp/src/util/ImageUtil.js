export default {
  getImageName (featureImages, key, item) {
    let itemName = this.getImageIdentifier(key, item)
    if (featureImages[itemName]) {
      return '/static/img/noimage.png'
    } else {
      // random is workaround for reload on new image
      return '/static/img/features/' + itemName + '.png' + '?' + Math.random()
    }
  },
  getImageIdentifier (key, item) {
    return key + this.getFeatureName(item)
  },
  getFeatureName (item) {
    if (typeof item === 'object') {
      return item.name
    } else {
      return item
    }
  },
  noImageHandler (featureImages, key, item, event) {
    event.preventDefault()
    featureImages[this.getImageIdentifier(key, item)] = true
  }
}
