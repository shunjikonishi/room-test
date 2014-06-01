module.exports = function(grunt) {
    'use strict';

    grunt.initConfig({
        copy: {
            framework: {
                files: [{
                    expand: true,
                    cwd : "../roomframework/dist/",
                    src : "roomframework.*", 
                    dest : "public/javascripts/ext/roomframework/"
                }]
            }
        },

        concat: {
            dist : {
                src : [
                    "../roomframework/src/room.utils.js",
                    "../roomframework/src/room.connection.js",
                    "../roomframework/src/room.cache.js",
                    "../roomframework/src/room.logger.js"
                ],
                dest: "../roomframework/dist/roomframework.js"
            }
        },

        uglify: {
            build: {
                files: [{
                    "../roomframework/dist/room.utils.min.js": "../roomframework/src/room.utils.js",
                    "../roomframework/dist/room.connection.min.js": "../roomframework/src/room.connection.js",
                    "../roomframework/dist/room.cache.min.js": "../roomframework/src/room.cache.js",
                    "../roomframework/dist/room.logger.min.js": "../roomframework/src/room.logger.js",
                    "../roomframework/dist/roomframework.min.js": "../roomframework/dist/roomframework.js"
                }]
            }
        },

        jshint : {
            all : ['../roomframework/src/*.js']
        },
        
        watch: {
            framework: {
                files: [
                    '../roomframework/src/*.js'
                ],
                tasks: ['jshint', 'concat', 'uglify', 'copy'],
            },
            web: {
                files: [
                    "app/**/*",
                    "public/**/*"
                ],
                tasks: [],
                options: {
                    livereload: true
                }
            }
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