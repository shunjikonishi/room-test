module.exports = function(grunt) {
    'use strict';

    grunt.initConfig({
        copy: {
            src2sandbox: {
                files: [{
                    expand: true,
                    cwd : "roomframework/src/",
                    src : "**", 
                    dest : "room-sandbox/gh-pages/sample/javascripts/"
                }]
            },
            dist2sandbox: {
                files: [{
                    expand: true,
                    cwd : "roomframework/dist/",
                    src : "**", 
                    dest : "room-sandbox/gh-pages/sample/javascripts/"
                }]
            },
            dist2test: {
                files: [{
                    expand: true,
                    cwd : "roomframework/dist/",
                    src : "roomframework.*", 
                    dest : "room-test/public/javascripts/ext/roomframework/"
                }]
            }
        },

        concat: {
            dist : {
                src : [
                    "roomframework/src/room.connection.js",
                    "roomframework/src/room.cache.js",
                    "roomframework/src/room.logger.js"
                ],
                dest: "roomframework/dist/roomframework.js"
            }
        },

        uglify: {
            build: {
                files: [{
                    "roomframework/dist/room.connection.min.js": "roomframework/src/room.connection.js",
                    "roomframework/dist/room.cache.min.js": "roomframework/src/room.cache.js",
                    "roomframework/dist/room.logger.min.js": "roomframework/src/room.logger.js",
                    "roomframework/dist/roomframework.min.js": "roomframework/dist/roomframework.js"
                }]
            }
        },

        jshint : {
            all : ['roomframework/src/*.js']
        },
        
        watch: {
            files: [
                'roomframework/src/*.js'
            ],
            tasks: ['jshint', 'concat', 'uglify', 'copy']
        }
    });
 
    // プラグインのロード
    grunt.loadNpmTasks('grunt-contrib-copy');
    grunt.loadNpmTasks('grunt-contrib-concat');
    grunt.loadNpmTasks('grunt-contrib-jshint');
    grunt.loadNpmTasks('grunt-contrib-uglify');
    grunt.loadNpmTasks('grunt-contrib-watch');
 
    // デフォルトタスクの設定
    grunt.registerTask('default', [ 'concat', 'uglify', 'copy']);
 
};