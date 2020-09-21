
document.getElementById('addBtn').addEventListener('click', addTopic);

console.log("hi");


var topicList = document.getElementById('topics');

function addTopic() {
    console.log("adding topic");
    var newTopic = document.getElementById('newTpc').value;
    console.log("new topic is:" + newTopic);
    var newTopicElement = document.createElement('li');
    newTopicElement.appendChild(document.createTextNode(newTopic));
    topicList.appendChild(newTopicElement);
}
